package com.needus.ecommerce.config.security;

import com.needus.ecommerce.exceptions.UserDisabledException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof DisabledException) {
            log.info("iovnzonzoxvlxncobpnapbvxzpbxopjbopxbxfc");
            getRedirectStrategy().sendRedirect(request, response, "/login?disabled=true");
        } else {
            log.info("iovnzonzoxvlxncobpnapbvxzpbxopjbopxbxfc");
            getRedirectStrategy().sendRedirect(request, response, "/login?error");
        }
    }
}
