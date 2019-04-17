package com.transfers.domain;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private LocalDateTime postedOn;
    private String errorMessage;
    private Payment payment;
}

