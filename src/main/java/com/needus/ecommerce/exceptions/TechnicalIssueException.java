package com.needus.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TechnicalIssueException extends RuntimeException{
    public TechnicalIssueException(String message, Exception cause) {
        super(message,cause.getCause());
    }
}
