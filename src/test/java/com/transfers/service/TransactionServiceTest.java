package com.transfers.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.domain.Payment;
import com.transfers.domain.Transaction;
import com.transfers.repository.TransactionRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Provider;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TransactionServiceTest {
    private Injector injector = getInjector();

    private TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private TransactionService transactionService = injector.getInstance(TransactionService.class);

    @Test
    public void insertTransaction_transactionRepository_called() {
        transactionService.insertTransaction(-1L);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).insert(captor.capture());
        assertEquals(-1L, (long)captor.getValue().getPaymentId());
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TransactionService.class);
                bind(TransactionRepository.class).toProvider(new Provider<TransactionRepository>() {
                    public TransactionRepository get() {
                        return transactionRepository;
                    }
                });
            }
        });
    }
}