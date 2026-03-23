package com.example.bankcards.entity;

import com.example.bankcards.enums.Role;
import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность, представляющая пользователя (клиента или администратора) системы.
 * Хранит данные для аутентификации и определяет уровень доступа через роли.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальное имя пользователя (логин) для входа в систему.
     * Поле обязательно для заполнения и не может дублироваться.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Хешированный пароль пользователя.
     * В базе данных хранится в зашифрованном виде (BCrypt).
     */
    @Column(nullable = false)
    private String password;

    /**
     * Роль пользователя в системе (например, USER или ADMIN).
     * Определяет права доступа к эндпоинтам контроллеров.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
