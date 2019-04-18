package com.transfers.service;

import com.transfers.domain.TransactionPosting;
import com.transfers.repository.TransactionPostingRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
public class TransactionPostingService {
    @Inject
    private TransactionPostingRepository transactionPostingRepository;

    public void insertTransactionPosting(Long transactionId, Long accountId, BigDecimal debit, BigDecimal credit){
        TransactionPosting transactionPosting = TransactionPosting.builder()
                .transactionId(transactionId)
                .accountId(accountId)
                .debit(debit)
                .credit(credit)
                .build();
        transactionPostingRepository.insert(transactionPosting);
    }
}
