package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

/**
 * Базовый абстрактный класс для всех кастомных исключений REST API.
 * Наследуется от {@link RuntimeException} и заставляет дочерние классы
 * определять HTTP-статус и внутренний код ошибки для унификации ответов.
 */
public abstract class BaseRestException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message текст ошибки, который будет передан в финальный ответ
     */
    public BaseRestException(String message) {
        super(message);
    }

    /**
     * Возвращает HTTP-статус, соответствующий данному исключению.
     *
     * @return объект {@link HttpStatus} (например, NOT_FOUND или BAD_REQUEST)
     */
    abstract HttpStatus getStatus();

    /**
     * Извлекает детальное сообщение об ошибке.
     *
     * @return строка с описанием причины исключения
     */
    public String getMessage() {
        return super.getMessage();
    }

    /**
     * Возвращает внутренний бизнес-код ошибки.
     * Помогает фронтенду идентифицировать конкретную проблему без разбора текста.
     *
     * @return целочисленный код ошибки
     */
    abstract int getCode();
}
