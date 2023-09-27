package com.needus.ecommerce.config;

import com.needus.ecommerce.entity.user.Role;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.repository.user.UserInformationRepository;
import com.needus.ecommerce.service.user.UserInformationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    UserInformationService service;
    @Autowired
    BCryptPasswordEncoder encoder;
    @Autowired
    UserInformationRepository repository;
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {
        UserInformation userInformation = new UserInformation();
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        String redirectUrl = null;
        if(authentication.getPrincipal() instanceof DefaultOAuth2User){
            DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
            String username = user.getAttribute("name");
            String password = encoder.encode(":<>?|}zx#$%^&&%^&%$%%#@#$%^&oianhbo1023@QE!4[]xcz.,><>12311u0rdfwhdoiv");
            String email = user.getAttribute("email");
            if(!repository.existsByEmail(email)) {
                userInformation.setUsername(username);
                userInformation.setPassword(password);
                userInformation.setEmail(email);
                userInformation.setEnabled(true);
                service.save(userInformation);
            }
        }
        if(roles.contains(new SimpleGrantedAuthority(Role.ADMIN.name())))
            response.sendRedirect("/admin/users/list");
        else
            response.sendRedirect("/shop/home");
    }
}
