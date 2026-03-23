package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями пользователей {@link User}.
 * Обеспечивает стандартные операции управления данными (CRUD) и
 * специализированный поиск для процессов аутентификации.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по его уникальному имени (логину).
     * Используется в фильтрах безопасности и сервисах авторизации
     * для загрузки данных пользователя.
     *
     * @param username уникальное имя пользователя в системе
     * @return {@link Optional}, содержащий сущность пользователя, если он найден
     */
    Optional<User> findByUsername(String username);
}
