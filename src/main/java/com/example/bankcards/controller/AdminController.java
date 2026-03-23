package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для административных операций.
 * Предоставляет API для управления всеми банковскими картами и пользователями системы.
 * Доступ ограничен только для пользователей с ролью 'ADMIN'.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CardService cardService;
    private final UserRepository userRepository;

    /**
     * Получение списка всех карт в системе с возможностью фильтрации по статусу.
     *
     * @param status   необязательный фильтр по статусу карты (ACTIVE, BLOCKED и т.д.)
     * @param pageable параметры пагинации (размер страницы, номер страницы, сортировка)
     * @return страница со списком DTO всех найденных карт
     */
    @GetMapping("/cards")
    public Page<CardResponseDTO> getAllCards(
            @RequestParam(required = false) CardStatus status,
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {
        return cardService.getAllCards(status, pageable);
    }

    /**
     * Принудительная блокировка карты по её идентификатору.
     *
     * @param id уникальный идентификатор карты
     */
    @PatchMapping("/cards/{id}/block")
    public void blockCard(@PathVariable Long id) {
        cardService.updateCardStatus(id, CardStatus.BLOCKED);
    }

    /**
     * Активация заблокированной карты.
     *
     * @param id уникальный идентификатор карты
     */
    @PatchMapping("/cards/{id}/activate")
    public void activateCard(@PathVariable Long id) {
        cardService.updateCardStatus(id, CardStatus.ACTIVE);
    }

    /**
     * Удаление карты из системы.
     *
     * @param id уникальный идентификатор карты
     */
    @DeleteMapping("/cards/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    /**
     * Получение полного списка зарегистрированных пользователей.
     *
     * @return список объектов сущности User
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Удаление пользователя из системы по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     */
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Изменение роли пользователя.
     *
     * @param id      уникальный идентификатор пользователя
     * @param newRole новая роль для назначения
     * @throws java.util.NoSuchElementException если пользователь с данным id не найден
     */
    @PatchMapping("/users/{id}/role")
    public void changeRole(@PathVariable Long id, @RequestParam Role newRole) {
        User user = userRepository.findById(id).orElseThrow();
        user.setRole(newRole);
        userRepository.save(user);
    }

    /**
     * Выпуск (создание) новой банковской карты для конкретного пользователя.
     *
     * @param userId идентификатор пользователя, для которого выпускается карта
     * @return DTO созданной карты со сгенерированным номером и балансом
     */
    @PostMapping("/cards/{userId}")
    public CardResponseDTO createCard(@PathVariable Long userId) {
        return cardService.createCard(userId);
    }
}
