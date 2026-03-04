package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.mapper.CardMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    public CardResponseDTO createCard(Long userId) {
        return createCardForUser(userId);
    }

    public CardResponseDTO createCardForUser(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        String fullNumber = generateRandomCardNumber();
        Card card = Card.builder()
                .cardNumber(fullNumber)
                .owner(owner)
                .balance(BigDecimal.ZERO)
                .status(CardStatus.ACTIVE)
                .expiryDate(LocalDate.now().plusYears(5))
                .build();
        Card savedCard = cardRepository.save(card);
        return cardMapper.toDto(savedCard);
    }

    public String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public void updateCardStatus(Long cardId, CardStatus newStatus) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("The card was not found"));
        card.setStatus(newStatus);
        cardRepository.save(card);
    }

    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new EntityNotFoundException("The card was not found");
        }
        cardRepository.deleteById(cardId);
    }

    public Page<CardResponseDTO> getAllCards(CardStatus status,Pageable pageable) {
        if (status != null) {
            return cardRepository.findAllByStatus(status, pageable).map(cardMapper::toDto);
        }
        return cardRepository.findAll(pageable).map(cardMapper::toDto);
    }

    @Transactional
    public void blockMyCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("The card was not found"));

        if (!card.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("You can't block someone else's card");
        }

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    public Page<CardResponseDTO> getMyCards(Long userId, CardStatus status, Pageable pageable) {
        Page<Card> cards;

        if (status != null) {
            cards = cardRepository.findAllByOwnerIdAndStatus(userId, status, pageable);
        } else {
            cards = cardRepository.findAllByOwnerId(userId, pageable);
        }
        return cards.map(cardMapper::toDto);
    }

    @Transactional
    public void transferBetweenOwnCards(Long fromCardId, Long toCardId, BigDecimal amount, User currentUser) {
        if (fromCardId.equals(toCardId)) {
            throw new CardOperationException("Cannot transfer money to the same card");
        }

        Long firstId = Math.min(fromCardId, toCardId);
        Long secondId = Math.max(fromCardId, toCardId);

        cardRepository.findByIdForUpdate(firstId);
        cardRepository.findByIdForUpdate(secondId);

        Card cardFrom = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new CardOperationException("Debit card not found"));
        Card cardTo = cardRepository.findById(toCardId)
                .orElseThrow(() -> new CardOperationException("The transfer card was not found"));

        if (!cardFrom.getOwner().getId().equals(currentUser.getId()) ||
                !cardTo.getOwner().getId().equals(currentUser.getId())) {
            throw new CardOperationException("Both cards must belong to the current user");
        }

        if (cardFrom.getStatus() != CardStatus.ACTIVE || cardTo.getStatus() != CardStatus.ACTIVE) {
            throw new CardOperationException("Both cards must be active");
        }

        if (cardFrom.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds on the card " + cardFrom.getCardNumber());
        }

        if (cardFrom.getExpiryDate().isBefore(LocalDate.now())) {
            cardFrom.setStatus(CardStatus.EXPIRED);
            cardRepository.save(cardFrom);
            throw new CardOperationException("The card has expired");
        }

        cardFrom.setBalance(cardFrom.getBalance().subtract(amount));
        cardTo.setBalance(cardTo.getBalance().add(amount));

        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);
    }

    public CardResponseDTO getCardBalance(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("he card was not found"));

        if (!card.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Access is denied");
        }
        return cardMapper.toDto(card);
    }
}
