package com.transfers.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Transaction {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private LocalDateTime postedOn;
    private String errorMessage;
    private Payment payment;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TransactionPosting> transactionPostings;
}

