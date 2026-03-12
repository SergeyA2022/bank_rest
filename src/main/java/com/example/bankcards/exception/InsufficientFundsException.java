package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends BaseRestException {
    public InsufficientFundsException(String message) {
        super(message);
    }

    @Override
    HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    @Override
    int getCode() {
        return 422;
    }
}
