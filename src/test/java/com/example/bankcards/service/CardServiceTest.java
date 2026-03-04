package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private final CardMapper cardMapper = Mappers.getMapper(CardMapper.class);

    private User testUser;
    private Card activeCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        activeCard = new Card();
        activeCard.setId(10L);
        activeCard.setOwner(testUser);
        activeCard.setBalance(new BigDecimal("1000.00"));
        activeCard.setStatus(CardStatus.ACTIVE);
        activeCard.setCardNumber("1234567812345678");
        activeCard.setExpiryDate(LocalDate.now().plusYears(1));
        cardService = new CardServiceImpl(cardRepository, userRepository, cardMapper);

        toCard = new Card();
        toCard.setId(11L);
        toCard.setOwner(testUser);
        toCard.setBalance(BigDecimal.ZERO);
        toCard.setStatus(CardStatus.ACTIVE);
    }

    @Test
    void createCard_Success_ShouldGenerate16DigitNumber() {

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArguments()[0]);

        CardResponseDTO response = cardService.createCard(testUser.getId());

        assertNotNull(response);
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("**** **** **** " + response.getMaskedNumber().substring(response.getMaskedNumber().length() - 4),
                response.getMaskedNumber());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void updateCardStatus_ShouldThrowException_WhenCardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cardService.updateCardStatus(1L, CardStatus.BLOCKED));

        assertEquals("The card was not found", exception.getMessage());
    }

    @Test
    void deleteCard_ShouldThrowException_IfNotExist() {
        when(cardRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> cardService.deleteCard(99L));
        verify(cardRepository, never()).deleteById(any());
    }

    @Test
    void getAllCards_WithStatus_ShouldReturnFilteredPage() {
        Pageable pageable = PageRequest.of(0, 10);
        CardStatus status = CardStatus.ACTIVE;

        Page<Card> cardPage = new PageImpl<>(List.of(activeCard));

        when(cardRepository.findAllByStatus(eq(status), eq(pageable))).thenReturn(cardPage);

        Page<CardResponseDTO> result = cardService.getAllCards(status, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(cardRepository).findAllByStatus(status, pageable);
        verify(cardRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllCards_WithoutStatus_ShouldReturnAllCards() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(activeCard));

        when(cardRepository.findAll(pageable)).thenReturn(cardPage);

        Page<CardResponseDTO> result = cardService.getAllCards(null, pageable);

        assertNotNull(result);
        verify(cardRepository).findAll(pageable);
        verify(cardRepository, never()).findAllByStatus(any(), any());
    }

    @Test
    void blockMyCard_Success() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(activeCard));

        cardService.blockMyCard(10L, 1L);

        assertEquals(CardStatus.BLOCKED, activeCard.getStatus());
        verify(cardRepository).save(activeCard);
    }

    @Test
    void blockMyCard_Fail_WrongOwner() {
        activeCard.getOwner().setId(999L);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(activeCard));

        assertThrows(AccessDeniedException.class, () ->
                cardService.blockMyCard(10L, 1L));
    }

    @Test
    void transfer_Success() {
        toCard.setExpiryDate(LocalDate.now().plusYears(1));
        when(cardRepository.findById(10L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.findById(11L)).thenReturn(Optional.of(toCard));

        cardService.transferBetweenOwnCards(10L, 11L, new BigDecimal("500.00"), testUser);

        assertEquals(new BigDecimal("500.00"), activeCard.getBalance());
        assertEquals(new BigDecimal("500.00"), toCard.getBalance());
    }

    @Test
    void transfer_Fail_ExpiredCard() {
        activeCard.setExpiryDate(LocalDate.now().minusDays(1));

        when(cardRepository.findById(10L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.findById(11L)).thenReturn(Optional.of(toCard));

        CardOperationException exception = assertThrows(CardOperationException.class, () ->
                cardService.transferBetweenOwnCards(10L, 11L, new BigDecimal("100.00"), testUser)
        );

        assertEquals("The card has expired", exception.getMessage());
        assertEquals(CardStatus.EXPIRED, activeCard.getStatus());
        verify(cardRepository).save(activeCard);
    }

    @Test
    void transfer_Fail_InsufficientFunds() {
        Card toCard = new Card();
        toCard.setOwner(testUser);
        toCard.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(10L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.findById(11L)).thenReturn(Optional.of(toCard));

        assertThrows(InsufficientFundsException.class, () ->
                cardService.transferBetweenOwnCards(10L, 11L, new BigDecimal("2000.00"), testUser));
    }

    @Test
    void getMyCards_ShouldInvokeRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(cardRepository.findAllByOwnerId(1L, pageable)).thenReturn(Page.empty());

        cardService.getMyCards(1L, null, pageable);

        verify(cardRepository).findAllByOwnerId(1L, pageable);
    }
}
