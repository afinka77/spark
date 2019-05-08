package com.transfers.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.api.dto.PaymentDto;
import com.transfers.domain.Account;
import com.transfers.domain.Payment;
import com.transfers.domain.enums.PaymentStatus;
import com.transfers.repository.PaymentRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import spark.HaltException;

import javax.inject.Provider;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class PaymentExecutionServiceTest {
    private Injector injector = getInjector();

    private PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private AccountService accountService = mock(AccountService.class);
    private TransactionService transactionService = mock(TransactionService.class);
    private TransactionPostingService transactionPostingService = mock(TransactionPostingService.class);
    private PaymentExecutionService paymentExecutionService = injector.getInstance(PaymentExecutionService.class);

    @Test
    public void validateAndCreatePayment_validPayment_created() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.TEN)
                .build();
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(account);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(account);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        paymentExecutionService.validateAndCreatePayment(1L, paymentDto);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).insert(captor.capture(), eq(2L), eq(2L));
        assertEquals(BigDecimal.TEN, captor.getValue().getAmount());
    }

    @Test(expected = HaltException.class)
    public void validateAndCreatePayment_zeroAmount_halt() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.ZERO)
                .build();
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(account);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(account);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        paymentExecutionService.validateAndCreatePayment(1L, paymentDto);

        verifyZeroInteractions(paymentRepository);
    }

    @Test(expected = HaltException.class)
    public void validateAndCreatePayment_negativeAmount_halt() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.valueOf(-1000,2))
                .build();
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(account);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(account);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        paymentExecutionService.validateAndCreatePayment(1L, paymentDto);

        verifyZeroInteractions(paymentRepository);
    }

    @Test(expected = HaltException.class)
    public void validateAndCreatePayment_fromAccountNotFound_halt() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.TEN)
                .build();
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(null);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(account);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        paymentExecutionService.validateAndCreatePayment(1L, paymentDto);

        verifyZeroInteractions(paymentRepository);
    }

    @Test(expected = HaltException.class)
    public void validateAndCreatePayment_fromAccountNotBelongToCustomer_halt() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.TEN)
                .build();
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(account);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(account);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        paymentExecutionService.validateAndCreatePayment(2L, paymentDto);

        verifyZeroInteractions(paymentRepository);
    }

    @Test(expected = HaltException.class)
    public void validateAndCreatePayment_toAccountIsNull_halt() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.TEN)
                .build();
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(account);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(null);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        paymentExecutionService.validateAndCreatePayment(1L, paymentDto);

        verifyZeroInteractions(paymentRepository);
    }

    @Test
    public void validateAndExecutePayment_paymentId_success() {
        Payment payment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.PENDING)
                .creditorAccountName("2")
                .debtorAccountName("3")
                .amount(BigDecimal.ONE)
                .build();
        Account account1 = Account.builder()
                .id(2L)
                .customerId(1L)
                .totalBalance(BigDecimal.TEN)
                .build();
        Account account2 = Account.builder()
                .id(3L)
                .customerId(2L)
                .totalBalance(BigDecimal.TEN)
                .build();
        when(accountService.getAccountByName("2")).thenReturn(account1);
        when(accountService.getAccountByName("3")).thenReturn(account2);
        when(paymentRepository.getPayment(1L, 2L)).thenReturn(payment);
        when(transactionService.insertTransaction(2L)).thenReturn(3L);

        paymentExecutionService.validateAndExecutePayment(1L, 2L);

        verify(transactionService).insertTransaction(2L);
        verify(transactionPostingService).insertTransactionPosting(3L, 3L, BigDecimal.ONE, BigDecimal.ZERO);
        verify(transactionPostingService).insertTransactionPosting(3l, 2L, BigDecimal.ZERO, BigDecimal.ONE);
        verify(accountService).updateBalance(2L, BigDecimal.TEN.subtract(BigDecimal.ONE));
        verify(accountService).updateBalance(3L, BigDecimal.TEN.add(BigDecimal.ONE));
        verify(paymentRepository).updateStatus(2L, PaymentStatus.SUCCESS, null);
    }

    @Test(expected = HaltException.class)
    public void validateAndExecutePayment_paymentNotFound_halt() {
        when(paymentRepository.getPayment(1L, 2L)).thenReturn(null);

        paymentExecutionService.validateAndExecutePayment(1L, 2L);

        verifyZeroInteractions(accountService);
        verifyZeroInteractions(transactionService);
        verifyZeroInteractions(transactionPostingService);
    }

    @Test(expected = HaltException.class)
    public void executePayment_paymentStatusNotPending_halt() {
        Payment payment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.SUCCESS)
                .creditorAccountName("2")
                .debtorAccountName("3")
                .amount(BigDecimal.ONE)
                .build();
        when(paymentRepository.getPayment(1L, 2L)).thenReturn(payment);

        paymentExecutionService.validateAndExecutePayment(1L, 2L);

        verifyZeroInteractions(accountService);
        verifyZeroInteractions(transactionService);
        verifyZeroInteractions(transactionPostingService);
    }

    @Test(expected = HaltException.class)
    public void validateAndExecutePayment_insufficientAccountBalance_halt() {
        Payment payment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.PENDING)
                .creditorAccountName("2")
                .debtorAccountName("3")
                .amount(BigDecimal.valueOf(1001, 2))
                .build();
        Account account1 = Account.builder()
                .id(2L)
                .customerId(1L)
                .totalBalance(BigDecimal.TEN)
                .build();
        Account account2 = Account.builder()
                .id(3L)
                .customerId(2L)
                .totalBalance(BigDecimal.TEN)
                .build();
        when(accountService.getAccountByName("2")).thenReturn(account1);
        when(accountService.getAccountByName("3")).thenReturn(account2);
        when(paymentRepository.getPayment(1L, 2L)).thenReturn(payment);

        paymentExecutionService.validateAndExecutePayment(1L, 2L);

        verifyZeroInteractions(transactionService);
        verifyZeroInteractions(transactionPostingService);
    }


    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(PaymentExecutionService.class);
                bind(PaymentRepository.class).toProvider(new Provider<PaymentRepository>() {
                    public PaymentRepository get() {
                        return paymentRepository;
                    }
                });
                bind(AccountService.class).toProvider(new Provider<AccountService>() {
                    public AccountService get() {
                        return accountService;
                    }
                });
                bind(TransactionService.class).toProvider(new Provider<TransactionService>() {
                    public TransactionService get() {
                        return transactionService;
                    }
                });
                bind(TransactionPostingService.class).toProvider(new Provider<TransactionPostingService>() {
                    public TransactionPostingService get() {
                        return transactionPostingService;
                    }
                });
            }
        });
    }

}