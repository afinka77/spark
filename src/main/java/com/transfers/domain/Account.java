package com.transfers.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Account {
    private String id;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    private BigDecimal totalBalance;
    private BigDecimal reservedBalance;
    private String name;
}
