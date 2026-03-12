package com.example.bankcards.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorDTO {
    private final HttpStatus status;
    private final int code;
    private final String message;
}
