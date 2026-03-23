package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое при неудачной попытке аутентификации пользователя.
 * Применяется в случаях, когда указанное имя пользователя не найдено в системе
 * или предоставленный пароль не соответствует сохраненному хешу.
 */
public class InvalidPasswordException extends BaseRestException {

    /**
     * Создает новое исключение с описанием ошибки аутентификации.
     *
     * @param message сообщение об ошибке, которое будет передано в теле ответа API.
     *                Рекомендуется использовать обобщенные сообщения для предотвращения
     *                утечки информации о существующих логинах.
     */
    public InvalidPasswordException(String message) {
        super(message);
    }

    /**
     * Возвращает HTTP-статус для данной ошибки.
     *
     * @return {@link HttpStatus#UNAUTHORIZED} (401), так как запрос не прошел проверку подлинности
     */
    @Override
    HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    /**
     * Возвращает внутренний бизнес-код ошибки приложения.
     *
     * @return целочисленный код 401 для идентификации ошибок входа
     */
    @Override
    int getCode() {
        return 401;
    }
}