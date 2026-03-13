package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorDTO;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(BaseRestException.class)
    public ResponseEntity<ErrorDTO> handleBaseRestException(BaseRestException ex) {
        ErrorDTO errorDTO = new ErrorDTO(ex.getStatus(), ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDTO, ex.getStatus());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> handleAuth() {
        return new ResponseEntity<>(
                new ErrorDTO(HttpStatus.UNAUTHORIZED, 401, "Доступ запрещен: требуется корректный JWT токен"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorDTO> handlePropertyError(PropertyReferenceException ex) {
        return new ResponseEntity<>(
                new ErrorDTO(HttpStatus.BAD_REQUEST, 400, "Передан некорректный параметр: " + ex.getPropertyName()),
                HttpStatus.BAD_REQUEST
        );
    }
}