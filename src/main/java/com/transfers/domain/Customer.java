package com.transfers.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Customer {
    private String id;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    private String name;
    private Account account;
    private UUID uuid;
}
