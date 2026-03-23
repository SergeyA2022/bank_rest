package com.example.bankcards.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Объект передачи данных (DTO) для отображения информации о банковской карте.
 * Содержит основные атрибуты карты, безопасные для передачи клиенту.
 */
@Data
public class CardResponseDTO {
    private Long id;

    /**
     * Маскированный номер карты (например, "**** 1234").
     * Используется для отображения в пользовательском интерфейсе в целях безопасности.
     */
    private String maskedNumber;

    /**
     * Текущий доступный баланс на счету карты
     */
    private BigDecimal balance;

    /**
     * Срок действия карты в формате строки (например, "MM/YY")
     */
    private String expiryDate;

    /**
     * Текущий статус карты (например, "ACTIVE", "BLOCKED")
     */
    private String status;
}
