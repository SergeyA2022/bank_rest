package com.example.bankcards.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки Swagger (OpenAPI).
 * Настраивает отображение документации API и интеграцию безопасности JWT.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создает и настраивает объект OpenAPI.
     * Добавляет глобальное требование безопасности (Security Requirement)
     * для всех эндпоинтов и регистрирует схему авторизации Bearer.
     *
     * @return настроенный объект OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createSecurityScheme()));
    }

    /**
     * Вспомогательный метод для создания схемы безопасности JWT.
     * Определяет тип аутентификации как HTTP Bearer с форматом JWT.
     *
     * @return объект схемы безопасности
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
