package com.transfers.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionPosting {
    private Long id;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    private Long accountId;
    private BigDecimal debit;
    private BigDecimal credit;
    private Long transactionId;
}
