package com.example.bankcards.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Утилитарный компонент для работы с JSON Web Tokens (JWT).
 * Отвечает за создание, разбор и валидацию токенов доступа.
 * Параметры секретного ключа и времени жизни токена загружаются из конфигурации приложения.
 */
@Component
public class JwtUtils {

    /** Секретный ключ для подписи токенов, заданный в application.yml */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** Время жизни токена в миллисекундах */
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    /**
     * Формирует криптографический ключ на основе секретной строки.
     *
     * @return объект {@link Key} для использования в алгоритмах HMAC
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Генерирует новый JWT токен для пользователя.
     * В токен записывается имя пользователя (subject), роль (claim),
     * дата выдачи и дата истечения.
     *
     * @param username имя пользователя
     * @param role роль пользователя в системе
     * @return строка, представляющая компактный JWT токен
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Извлекает имя пользователя (Subject) из предоставленного токена.
     *
     * @param token JWT токен
     * @return имя пользователя
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Извлекает роль пользователя из полезной нагрузки (claims) токена.
     *
     * @param token JWT токен
     * @return название роли в виде строки
     */
    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().get("role", String.class);
    }

    /**
     * Проверяет токен на валидность и отсутствие повреждений.
     * Проверка включает в себя сверку подписи и контроль срока годности.
     *
     * @param token строка токена
     * @return true, если токен корректен и активен; false в противном случае
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            // Токен может быть просрочен, подпись неверна или формат нарушен
            return false;
        }
    }
}
