package com.example.bankcards.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Универсальный объект передачи данных для описания ошибок API.
 * Используется в {@code GlobalRestExceptionHandler} для формирования
 * единообразных ответов при возникновении исключений.
 */
@Data
public class ErrorDTO {

    /**
     * HTTP-статус ответа (например, 400 BAD_REQUEST, 404 NOT_FOUND).
     * Помогает клиенту понять категорию ошибки.
     */
    private final HttpStatus status;

    /**
     * Внутренний код ошибки приложения.
     * Позволяет более точно идентифицировать проблему.
     */
    private final int code;

    /**
     * Текстовое описание ошибки.
     * Содержит понятное пользователю или разработчику сообщение о причине сбоя.
     */
    private final String message;
}
