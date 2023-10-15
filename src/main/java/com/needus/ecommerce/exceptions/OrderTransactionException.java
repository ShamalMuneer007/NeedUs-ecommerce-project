package com.needus.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class OrderTransactionException extends Throwable{
    public OrderTransactionException(String message) {
        super(message);
    }
}
