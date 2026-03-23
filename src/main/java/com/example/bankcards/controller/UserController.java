package com.example.bankcards.controller;


import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для пользовательских операций с банковскими картами.
 * Позволяет просматривать собственные карты, проверять баланс, блокировать карты
 * и осуществлять переводы между ними.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final CardService cardService;
    private final UserRepository userRepository;

    /**
     * Получение списка всех карт текущего аутентифицированного пользователя.
     *
     * @param username имя пользователя, извлеченное из Principal
     * @param status   необязательный фильтр по статусу карты
     * @param pageable параметры пагинации и сортировки
     * @return страница со списком карт пользователя
     */
    @GetMapping("/cards")
    public Page<CardResponseDTO> getMyCards(
            @AuthenticationPrincipal String username,
            @RequestParam(required = false) CardStatus status,
            Pageable pageable) {

        return cardService.getMyCards(getUser(username).getId(), status, pageable);
    }

    /**
     * Блокировка собственной карты пользователя.
     *
     * @param id       идентификатор карты
     * @param username имя пользователя для проверки прав владения
     */
    @PatchMapping("/cards/{id}/block")
    @ResponseStatus(HttpStatus.OK)
    public void blockMyCard(@PathVariable Long id, @AuthenticationPrincipal String username) {
        cardService.blockMyCard(id, getUser(username).getId());
    }

    /**
     * Перевод средств между собственными картами пользователя.
     *
     * @param transferDTO объект с данными о картах и сумме перевода
     * @param username    имя пользователя, инициировавшего перевод
     */
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public void transfer(@RequestBody TransferDTO transferDTO, @AuthenticationPrincipal String username) {

        cardService.transferBetweenOwnCards(
                transferDTO.getFromCardId(),
                transferDTO.getToCardId(),
                transferDTO.getAmount(),
                getUser(username)
        );
    }

    /**
     * Получение актуального баланса и информации по конкретной карте.
     *
     * @param cardId   идентификатор карты
     * @param username имя пользователя для верификации доступа
     * @return DTO с информацией о балансе и номере карты
     */
    @GetMapping("/{cardId}/balance")
    public CardResponseDTO getBalance(
            @PathVariable Long cardId,
            @AuthenticationPrincipal String username) {

        return cardService.getCardBalance(cardId, getUser(username).getId());
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CardOperationException("User not found"));
    }
}
