package com.transfers.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDto {
    private BigDecimal amount;
    private String message;
    private String fromAccount;
    private String toAccount;
}
