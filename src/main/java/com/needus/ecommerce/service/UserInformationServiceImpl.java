package com.needus.ecommerce.service;

import com.needus.ecommerce.entity.Role;
import com.needus.ecommerce.entity.UserInformation;
import com.needus.ecommerce.repository.ConfirmationTokenRepository;
import com.needus.ecommerce.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public UserInformation save(UserInformation user) {
        if(userRepository.count()<1){
            user.setRole(Role.ADMIN);
        }
        return userRepository.save(user);
    }

}
