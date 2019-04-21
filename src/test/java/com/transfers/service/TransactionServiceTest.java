package com.transfers.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.repository.TransactionRepository;
import org.junit.Test;

import javax.inject.Provider;

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

        verify(transactionRepository).insert(-1L);
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