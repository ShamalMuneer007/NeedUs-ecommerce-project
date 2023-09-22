package com.needus.ecommerce.exceptions;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler{
    @ExceptionHandler(DisabledException.class)
    public String handleDisabledException(DisabledException ex) {
        return "redirect:/login?disabled=true";
    }
}
