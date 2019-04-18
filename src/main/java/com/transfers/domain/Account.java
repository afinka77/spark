package com.transfers.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Account {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private BigDecimal totalBalance;
    private BigDecimal reservedBalance;
    private String name;
    @JsonIgnore
    private Long customerId;
}
