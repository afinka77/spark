package com.transfers.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionPosting {
    private Long id;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    private Transaction transaction;
    private Account account;
    private BigDecimal debit;
    private BigDecimal credit;
}
