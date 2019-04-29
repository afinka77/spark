package com.transfers.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.api.dto.PaymentDto;
import com.transfers.domain.Payment;
import com.transfers.repository.PaymentRepository;
import org.junit.Test;
import spark.HaltException;

import javax.inject.Provider;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PaymentServiceTest {
    private Injector injector = getInjector();

    private PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private PaymentExecutionService paymentExecutionService = mock(PaymentExecutionService.class);
    private PaymentService paymentService = injector.getInstance(PaymentService.class);

    @Test
    public void getPayments_customerId_called() {
        paymentService.getPayments("11");

        verify(paymentRepository).getPayments(11L);
    }

    @Test
    public void getPayment_validIds_called() {
        when(paymentRepository.getPayment(11L, 22L)).thenReturn(new Payment());
        paymentService.getPayment("11", "22");

        verify(paymentRepository).getPayment(11L, 22L);
    }

    @Test(expected = HaltException.class)
    public void getPayment_notExistingIds_halted() {
        paymentService.getPayment("11", "22");

        verify(paymentRepository).getPayment(11L, 22L);
    }

    @Test
    public void createPayment_validPayment_created() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.TEN)
                .build();
        Payment expected = Payment.builder()
                .id(1L)
                .build();
        when(paymentExecutionService.validateAndCreatePayment(1L, paymentDto)).thenReturn(1L);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(expected);

        Payment payment = paymentService.createPayment("1", paymentDto);

        verify(paymentExecutionService).validateAndCreatePayment(1L, paymentDto);
        verify(paymentRepository).getPayment(1L,1L);
        assertEquals(expected, payment);
    }

    @Test
    public void executePayment_paymentId_success() {
        Payment expected = Payment.builder()
                .id(2L)
                .build();
        when(paymentRepository.getPayment(1L, 2L)).thenReturn(expected);

        Payment payment = paymentService.executePayment("1", "2");

        verify(paymentExecutionService).validateAndExecutePayment(1L,2L);
        verify(paymentRepository).getPayment(1L,2L);
        assertEquals(expected, payment);
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(PaymentService.class);
                bind(PaymentRepository.class).toProvider(new Provider<PaymentRepository>() {
                    public PaymentRepository get() {
                        return paymentRepository;
                    }
                });
                bind(PaymentExecutionService.class).toProvider(new Provider<PaymentExecutionService>() {
                    public PaymentExecutionService get() {
                        return paymentExecutionService;
                    }
                });
            }
        });
    }
}