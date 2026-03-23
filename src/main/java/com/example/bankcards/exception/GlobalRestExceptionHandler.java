package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorDTO;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений для всех контроллеров приложения.
 * Перехватывает возникающие ошибки и преобразует их в унифицированный формат {@link ErrorDTO}.
 */
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    /**
     * Обработка кастомных исключений приложения, наследуемых от {@link BaseRestException}.
     * Использует внутренние поля исключения для формирования статуса и кода ошибки.
     *
     * @param ex объект перехваченного исключения
     * @return ответ с телом ErrorDTO и соответствующим HTTP-статусом
     */
    @ExceptionHandler(BaseRestException.class)
    public ResponseEntity<ErrorDTO> handleBaseRestException(BaseRestException ex) {
        ErrorDTO errorDTO = new ErrorDTO(ex.getStatus(), ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDTO, ex.getStatus());
    }

    /**
     * Обработка ошибок аутентификации Spring Security.
     * Срабатывает, когда передан неверный JWT токен или он отсутствует.
     *
     * @return ответ со статусом 401 UNAUTHORIZED и поясняющим сообщением
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> handleAuth() {
        return new ResponseEntity<>(
                new ErrorDTO(HttpStatus.UNAUTHORIZED, 401, "Доступ запрещен"),
                HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * Обработка ошибок маппинга полей (например, при неверной сортировке в Pageable).
     * Перехватывает системную ошибку Spring Data и указывает конкретное проблемное поле.
     *
     * @param ex исключение, содержащее информацию о неверном свойстве
     * @return ответ со статусом 400 BAD_REQUEST
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorDTO> handlePropertyError(PropertyReferenceException ex) {
        return new ResponseEntity<>(
                new ErrorDTO(HttpStatus.BAD_REQUEST, 400, "Передан некорректный параметр: " + ex.getPropertyName()),
                HttpStatus.BAD_REQUEST
        );
    }
}