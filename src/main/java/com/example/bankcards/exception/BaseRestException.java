package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseRestException extends RuntimeException {
    public BaseRestException(String message) {
        super(message);
    }

    abstract HttpStatus getStatus();

    public String getMessage() {
        return super.getMessage();
    }

    abstract int getCode();
}
