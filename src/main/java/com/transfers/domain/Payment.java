package com.transfers.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.transfers.domain.enums.PaymentMethod;
import com.transfers.domain.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class Payment {
    private Long id;
    private Timestamp createdOn;
    private Timestamp modifiedOn;
    private PaymentMethod method;
    private BigDecimal amount;
    private String message;
    private PaymentStatus status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
    @JsonIgnore
    private Long customerId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String debtorAccountName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String creditorAccountName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Transaction transaction;
}
