package com.transfers.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class Account {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private BigDecimal totalBalance;
    private BigDecimal reservedBalance;
    private String name;
    @JsonIgnore
    private Long customerId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TransactionPosting> transactionPostings;
}
