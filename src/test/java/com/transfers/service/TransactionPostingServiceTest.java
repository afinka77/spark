package com.transfers.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.domain.TransactionPosting;
import com.transfers.repository.TransactionPostingRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Provider;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TransactionPostingServiceTest {
    private Injector injector = getInjector();

    private TransactionPostingRepository transactionPostingRepository = mock(TransactionPostingRepository.class);
    private TransactionPostingService transactionPostingService = injector.getInstance(TransactionPostingService.class);

    @Test
    public void insertTransactionPosting_transactionPostingRepository_called() {
        transactionPostingService.insertTransactionPosting(1L, 2L, BigDecimal.ONE, BigDecimal.ZERO);

        ArgumentCaptor<TransactionPosting> captor = ArgumentCaptor.forClass(TransactionPosting.class);
        verify(transactionPostingRepository).insert(captor.capture());
        assertEquals(1L, (long) captor.getValue().getTransactionId());
        assertEquals(2L, (long) captor.getValue().getAccountId());
        assertEquals(BigDecimal.ONE, captor.getValue().getDebit());
        assertEquals(BigDecimal.ZERO, captor.getValue().getCredit());
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TransactionPostingService.class);
                bind(TransactionPostingRepository.class).toProvider(new Provider<TransactionPostingRepository>() {
                    public TransactionPostingRepository get() {
                        return transactionPostingRepository;
                    }
                });
            }
        });
    }
}