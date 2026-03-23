package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое при попытке совершить финансовую операцию
 * при недостаточном количестве денежных средств на балансе карты.
 *
 * Соответствует статусу {@link HttpStatus#UNPROCESSABLE_ENTITY} (422),
 * так как запрос семантически верен, но не может быть выполнен из-за состояния счета.
 */
public class InsufficientFundsException extends BaseRestException {

    /**
     * Создает новое исключение с описанием нехватки средств.
     *
     * @param message детальное сообщение об ошибке, передаваемое в ответ API
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    /**
     * Возвращает HTTP-статус для данной ошибки.
     *
     * @return статус 422 UNPROCESSABLE_ENTITY
     */
    @Override
    HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    /**
     * Возвращает внутренний бизнес-код ошибки приложения.
     *
     * @return числовой код 422 для идентификации финансовых ошибок
     */
    @Override
    int getCode() {
        return 422;
    }
}
