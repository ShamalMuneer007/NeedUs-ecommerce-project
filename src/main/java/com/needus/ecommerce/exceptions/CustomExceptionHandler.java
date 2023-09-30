package com.needus.ecommerce.exceptions;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class CustomExceptionHandler implements ErrorController {
    @ExceptionHandler(DisabledException.class)
    public String handleDisabledException(DisabledException ex) {
        return "redirect:/login?disabled=true";
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex){
        return "404";
    }
    @ExceptionHandler(TechnicalIssueException.class)
    public String technicalException(TechnicalIssueException ex){
        return "500";
    }
    @ExceptionHandler({NoHandlerFoundException.class})
    public String handleNoHandlerFoundException(NoHandlerFoundException ex){
        return "404";
    }

}
