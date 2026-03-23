package com.example.bankcards.entity;

import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.util.CryptoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сущность, представляющая банковскую карту в системе.
 * Хранит информацию о номере карты, владельце, сроке действия, статусе и текущем балансе.
 */
@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Полный номер банковской карты.
     * Данные шифруются при записи в БД и расшифровываются при чтении
     * с помощью {@link CryptoConverter}.
     */
    @Column(nullable = false)
    @Convert(converter = CryptoConverter.class)
    private String cardNumber;

    /**
     * Владелец данной карты.
     * Связь "много к одному" с сущностью {@link User}.
     */
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /** Дата окончания срока действия карты */
    private LocalDate expiryDate;

    /**
     * Текущий статус карты (например, ACTIVE, BLOCKED).
     * Хранится в базе данных как строковое значение.
     */
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    /** Текущий денежный баланс на счете карты */
    private BigDecimal balance;
}
