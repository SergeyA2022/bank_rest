package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Интерфейс сервиса для управления банковскими картами.
 * Определяет бизнес-логику для операций выпуска, блокировки, переводов
 * и администрирования карт.
 */
public interface CardService {

    CardResponseDTO createCard(Long userId);

    String generateRandomCardNumber();

    void updateCardStatus(Long cardId, CardStatus newStatus);

    void deleteCard(Long cardId);

    Page<CardResponseDTO> getAllCards(CardStatus status, Pageable pageable);

    void blockMyCard(Long cardId, Long userId);

    Page<CardResponseDTO> getMyCards(Long userId, CardStatus status, Pageable pageable);

    void transferBetweenOwnCards(Long fromCardId, Long toCardId, BigDecimal amount, User currentUser);

    CardResponseDTO getCardBalance(Long cardId, Long userId);
}
