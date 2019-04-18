package com.transfers.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class Customer {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Account> accounts;
}
