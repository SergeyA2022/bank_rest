package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Объект передачи данных (DTO) для выполнения операции перевода между картами.
 * Содержит идентификаторы счетов и сумму транзакции с встроенной валидацией.
 */
@Data
public class TransferDTO {

    /**
     * Уникальный идентификатор карты, с которой будут списаны средства.
     * Обязательное поле для заполнения.
     */
    @NotNull(message = "Debit card ID is required")
    private Long fromCardId;

    /**
     * Уникальный идентификатор карты, на которую будут зачислены средства.
     * Обязательное поле для заполнения.
     */
    @NotNull(message = "The credit card ID is required")
    private Long toCardId;

    /**
     * Сумма перевода.
     * Должна быть указана и быть строго больше нуля.
     */
    @NotNull(message = "The transfer amount is required")
    @Positive(message = "The amount must be greater than zero")
    private BigDecimal amount;
}
