package com.needus.ecommerce.exceptions;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class CustomExceptionHandler implements ErrorController {
    @ExceptionHandler(UserDisabledException.class)
    public String handleDisabledException(UserDisabledException ex) {
        return "redirect:/login?disabled=true";
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex){
        return "404";
    }
    @ExceptionHandler(TechnicalIssueException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String technicalException(TechnicalIssueException ex){
        return "500";
    }
    @ExceptionHandler({NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String NoHandlerFoundException(NoHandlerFoundException ex){
        return "404";
    }
    @ExceptionHandler({UnknownException.class})
    public String unknownException(UnknownException ex){return "UnknownError";}
    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accessDeniedException(AccessDeniedException ex){return "500";}

}
