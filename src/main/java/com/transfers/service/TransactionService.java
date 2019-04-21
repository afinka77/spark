package com.transfers.service;

import com.transfers.domain.Transaction;
import com.transfers.repository.TransactionRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionService {
    @Inject
    private TransactionRepository transactionRepository;

    public Long insertTransaction(Long paymentId){
        Transaction transaction = Transaction.builder()
                .paymentId(paymentId)
                .build();
        transactionRepository.insert(transaction);
        return transaction.getId();
    }
}
