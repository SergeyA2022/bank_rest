package com.example.bankcards.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CardResponseDTO {
    private Long id;
    private String maskedNumber;
    private BigDecimal balance;
    private String expiryDate;
    private String status;
}
