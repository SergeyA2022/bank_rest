package com.example.bankcards.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Кастомная точка входа для обработки ошибок аутентификации.
 * Реализует интерфейс {@link AuthenticationEntryPoint} для перехвата неавторизованных запросов (401 Unauthorized).
 *
 * Данный компонент служит "мостиком", который передает управление от фильтров безопасности
 * Spring Security к стандартному механизму обработки исключений Spring MVC.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Резолвер исключений, используемый для делегирования обработки ошибки
     * контроллеру исключений (GlobalRestExceptionHandler).
     */
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    /**
     * Метод запускается, когда неаутентифицированный пользователь пытается получить доступ
     * к защищенному ресурсу.
     *
     * @param request       объект HTTP-запроса
     * @param response      объект HTTP-ответа
     * @param authException исключение, вызвавшее сбой аутентификации
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {
        // Перенаправляет исключение аутентификации в GlobalRestExceptionHandler
        resolver.resolveException(request, response, null, authException);
    }
}
