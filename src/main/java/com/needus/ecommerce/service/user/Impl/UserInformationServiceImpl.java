package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.Wishlist;
import com.needus.ecommerce.entity.user.enums.Role;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.repository.user.ConfirmationTokenRepository;
import com.needus.ecommerce.repository.user.UserInformationRepository;
import com.needus.ecommerce.service.user.CartService;
import com.needus.ecommerce.service.user.UserInformationService;
import com.needus.ecommerce.service.user.WishlistService;
import com.needus.ecommerce.service.verification.ConfirmationTokenService;
import com.needus.ecommerce.service.verification.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
public class UserInformationServiceImpl implements UserInformationService {
    private final ConfirmationTokenService confirmationTokenService;
    private final BCryptPasswordEncoder encoder;
    private final UserInformationRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;

    private final SessionRegistry sessionRegistry;
    private final CartService cartService;
    private final WishlistService wishlistService;
    @Autowired
    public UserInformationServiceImpl(
        BCryptPasswordEncoder encoder, UserInformationRepository userRepository,
        ConfirmationTokenRepository confirmationTokenRepository, ConfirmationTokenService confirmationTokenService,
        EmailService emailService, SessionRegistry sessionRegistry, CartService cartService, WishlistService wishlistService){
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.sessionRegistry = sessionRegistry;
        this.cartService = cartService;
        this.wishlistService = wishlistService;
    }


    @Override
    public UserInformation register(UserInformation user) {
        Wishlist wishlist =  new Wishlist();
        Cart cart = new Cart();
        cartService.createCart(cart);
        wishlistService.createWishlist(wishlist);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(false);
        user.setUserWishlist(wishlist);
        user.setCart(cart);
        if(userRepository.count()<1){
            user.setRole(Role.ADMIN);
        }
        else {
            user.setRole(Role.USER);
        }
        Optional<UserInformation> saved = Optional.of(save(user));
        saved.ifPresent( mail -> {
                try {
                    String token = UUID.randomUUID().toString();
                    confirmationTokenService.save(saved.get(),token);
                    emailService.sendHtmlMail(mail);
                } catch (Exception e) {
                    log.error("Something wen wrong while sending user confirmation token");
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
    public void blockUser(UUID id, HttpServletRequest request, HttpServletResponse response) {
        UserInformation user = userRepository.findById(id).get();
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        log.info(""+allPrincipals);
        for (Object principal : allPrincipals) {
            if (principal instanceof UserDetails otherUserDetails) {
                if (otherUserDetails.getUsername().equals(user.getUsername())) {
                    // This user is logged in, so let's log them out.
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    for (SessionInformation session : sessions) {
                        session.expireNow();
                    }
                }
            }
        }
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
        if(Objects.nonNull(user.getPhoneNumber())&&!user.getPhoneNumber().equals(updateInfo.getPhoneNumber())){
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
    public boolean usersExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserInformation save(UserInformation user) {
//        if(userRepository.count()<1){
//            user.setRole(Role.ADMIN);
//        }
//        else {
//            user.setRole(Role.USER);
//        }
        user.setUserCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }


    @Override
    public UserInformation getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName());
    }
}
