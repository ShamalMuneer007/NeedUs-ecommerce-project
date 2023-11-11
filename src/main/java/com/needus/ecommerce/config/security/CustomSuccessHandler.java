package com.needus.ecommerce.config.security;

import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.Wallet;
import com.needus.ecommerce.entity.user.enums.Role;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.Wishlist;
import com.needus.ecommerce.exceptions.TechnicalIssueException;
import com.needus.ecommerce.repository.user.UserInformationRepository;
import com.needus.ecommerce.service.user.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    UserInformationService service;
    @Autowired
    CartService cartService;
    @Autowired
    WishlistService wishlistService;
    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    WalletService walletService;

    @Autowired
    UserLogService userLogService;
    @Autowired
    UserInformationRepository repository;
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        UserInformation userInformation = new UserInformation();
        try {
            if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
                DefaultOAuth2User OauthUser = (DefaultOAuth2User) authentication.getPrincipal();
                String username = OauthUser.getAttribute("name");
                SecureRandom random = new SecureRandom();
                String generatedPassword = new BigInteger(130, random).toString(32);
                String email = OauthUser.getAttribute("email");
                if (!service.userExistsByEmail(email)) {
                    Wishlist wishlist = new Wishlist();
                    Cart cart = cartService.createCart();
                    wishlistService.createWishlist(wishlist);
                    userInformation.setUsername(username);
                    userInformation.setPassword(encoder.encode(generatedPassword));
                    userInformation.setEmail(email);
                    userInformation.setEnabled(true);
                    userInformation.setRole(Role.USER);
                    userInformation.setUserWishlist(wishlist);
                    userInformation.setCart(cart);
                    userInformation = service.save(userInformation);
                    Wallet wallet = walletService.createWallet(userInformation);
                    userInformation.setWallet(wallet);
                    service.save(userInformation);

                }
                UserInformation userInfo = service.findUserByName(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userInfo.getUsername(), userInfo.getPassword(), List.of(new SimpleGrantedAuthority(Role.USER.name())));
                log.info("authToken : " + authToken);
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("Security Context holder : " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            }
        }
        catch (Exception e){
            log.error("Something went wrong while user logging in using google oauth");
            throw new TechnicalIssueException("Something went wrong while user logging in using google oauth");
        }
        userLogService.logUser(userInformation.getUsername(), request.getRemoteAddr());
        if (roles.contains(new SimpleGrantedAuthority(Role.ADMIN.name())))
            response.sendRedirect("/admin/dashboard/sales-report");
        else
            response.sendRedirect("/shop/home");
    }

}
