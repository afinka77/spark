package com.transfers.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class TransactionPosting {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private Long accountId;
    private BigDecimal debit;
    private BigDecimal credit;
    private Long transactionId;
}
