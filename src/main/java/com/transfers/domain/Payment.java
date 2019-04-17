package com.transfers.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Payment {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private String method;
    private BigDecimal amount;
    private String message;
    private String status;
    private String error_message;
    private Account debtorAccount;
    private Account creditorAccount;
}
