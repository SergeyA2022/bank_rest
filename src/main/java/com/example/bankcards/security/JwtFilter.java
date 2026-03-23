package com.example.bankcards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Фильтр для обработки JWT-токенов в каждом входящем HTTP-запросе.
 * Наследуется от {@link OncePerRequestFilter}, что гарантирует однократное выполнение
 * фильтра за один цикл запроса.
 *
 * Фильтр проверяет заголовок Authorization, валидирует токен и устанавливает
 * аутентификацию в {@link SecurityContextHolder}.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    /** Утилитарный класс для работы с JSON Web Token */
    private final JwtUtils jwtUtils;

    /**
     * Выполняет фильтрацию входящего запроса.
     * Проверяет наличие Bearer-токена, извлекает из него имя пользователя и роль,
     * после чего создает объект аутентификации.
     *
     * @param request     объект HTTP-запроса
     * @param response    объект HTTP-ответа
     * @param filterChain цепочка фильтров безопасности
     * @throws ServletException в случае ошибок обработки сервлета
     * @throws IOException      в случае ошибок ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Извлечение заголовка авторизации
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Проверка валидности токена и его подписи
            if (jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsernameFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);
                // Создание полномочия на основе роли из токена
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                // Формирование объекта аутентификации для контекста Spring Security
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(authority));
                // Установка аутентификации в глобальный контекст потока
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        // Передача управления следующему фильтру в цепочке
        filterChain.doFilter(request, response);
    }
}
