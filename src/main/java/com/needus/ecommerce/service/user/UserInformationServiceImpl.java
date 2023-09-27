package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.Role;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.repository.user.ConfirmationTokenRepository;
import com.needus.ecommerce.repository.user.UserInformationRepository;
import com.needus.ecommerce.service.verification.ConfirmationTokenService;
import com.needus.ecommerce.service.verification.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserInformationServiceImpl implements UserInformationService {
    private final ConfirmationTokenService confirmationTokenService;
    private final BCryptPasswordEncoder encoder;
    private final UserInformationRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;

    @Autowired
    public UserInformationServiceImpl(
        BCryptPasswordEncoder encoder, UserInformationRepository userRepository,
        ConfirmationTokenRepository confirmationTokenRepository, ConfirmationTokenService confirmationTokenService,
        EmailService emailService){
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
    }


    @Override
    public UserInformation register(UserInformation user) {
        user.setRole(Role.USER);
        user.setUserCreatedAt(LocalDateTime.now());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(false);
        Optional<UserInformation> saved = Optional.of(save(user));
        saved.ifPresent( mail -> {
                try {
                    String token = UUID.randomUUID().toString();
                    confirmationTokenService.save(saved.get(),token);
                    emailService.sendHtmlMail(mail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        return saved.get();
    }

    @Override
    public UserInformation findUserById(UUID id) {
        return userRepository.findById(id).get();
    }

    @Override
    public void blockUser(UUID id) {
        UserInformation user = userRepository.findById(id).get();
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(UUID id) {
        UserInformation user = userRepository.findById(id).get();
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void updateUser(UUID id, UserInformation user) {
        UserInformation updateInfo = userRepository.findById(id).get();
        if(!user.getUsername().equals(updateInfo.getUsername())||!user.getUsername().isEmpty()){
            updateInfo.setUsername(user.getUsername());
        }
        if(!user.getEmail().equals(updateInfo.getEmail())||!user.getEmail().isEmpty()){
            updateInfo.setEmail(user.getEmail());
        }
        if(!user.getPhoneNumber().equals(updateInfo.getPhoneNumber())||!user.getPhoneNumber().isEmpty()){
            updateInfo.setPhoneNumber(user.getPhoneNumber());
        }
        userRepository.save(updateInfo);
    }

    @Override
    public UserInformation findUserByName(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserInformation> findAllUsers() {
        return userRepository.findAllNonDeleted();
    }

    @Override
    public UserInformation save(UserInformation user) {
        if(userRepository.count()<1){
            user.setRole(Role.ADMIN);
        }
        return userRepository.save(user);
    }

}
