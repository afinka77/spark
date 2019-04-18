package com.transfers.domain;

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
    private Customer customer;
}
