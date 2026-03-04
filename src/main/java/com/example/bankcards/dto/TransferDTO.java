package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferDTO {

    @NotNull(message = "Debit card ID is required")
    private Long fromCardId;

    @NotNull(message = "The credit card ID is required")
    private Long toCardId;

    @NotNull(message = "The transfer amount is required")
    @Positive(message = "The amount must be greater than zero")
    private BigDecimal amount;
}
