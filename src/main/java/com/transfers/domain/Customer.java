package com.transfers.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Customer {
    private String id;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    private String name;
    @JsonIgnore
    private Account account;
    @JsonIgnore
    private UUID uuid;
}
