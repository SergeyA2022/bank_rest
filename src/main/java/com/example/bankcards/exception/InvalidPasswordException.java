package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BaseRestException {
    public InvalidPasswordException(String message) {
        super(message);
    }

    @Override
    HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    int getCode() {
        return 401;
    }
}