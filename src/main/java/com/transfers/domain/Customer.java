package com.transfers.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class Customer {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Account> accounts;
}
