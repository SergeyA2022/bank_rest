package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(BaseRestException.class)
    public ResponseEntity<ErrorDTO> handleBaseRestException(BaseRestException ex) {
        ErrorDTO errorDTO = new ErrorDTO(ex.getStatus(), ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDTO, ex.getStatus());
    }
}