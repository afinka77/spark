package com.transfers.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Customer {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private String name;
    private Account account;
}
