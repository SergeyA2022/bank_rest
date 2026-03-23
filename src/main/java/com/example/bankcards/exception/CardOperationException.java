package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое при возникновении ошибок в процессе операций с банковскими картами.
 * Применяется в случаях нарушения бизнес-логики (например, блокировка уже заблокированной карты,
 * отсутствие прав доступа к карте или неверный статус карты для совершения операции).
 */
public class CardOperationException extends BaseRestException {

    /**
     * Создает новое исключение с детальным описанием причины ошибки.
     *
     * @param message сообщение об ошибке, которое будет передано клиенту API
     */
    public CardOperationException(String message) {
        super(message);
    }

    /**
     * Возвращает HTTP-статус для данной ошибки.
     *
     * @return {@link HttpStatus#BAD_REQUEST} (400), так как это ошибка клиентского запроса или логики
     */
    @Override
    HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * Возвращает внутренний код ошибки приложения.
     *
     * @return целочисленный код 400 для идентификации ошибок операций
     */
    @Override
    int getCode() {
        return 400;
    }
}
