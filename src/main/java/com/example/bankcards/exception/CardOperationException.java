package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class CardOperationException extends BaseRestException {
    public CardOperationException(String message) {
        super(message);
    }

    @Override
    HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    int getCode() {
        return 400;
    }
}
