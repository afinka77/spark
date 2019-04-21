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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class PaymentServiceTest {
    private Injector injector = getInjector();

    private PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private AccountService accountService = mock(AccountService.class);
    private TransactionService transactionService = mock(TransactionService.class);
    private TransactionPostingService transactionPostingService = mock(TransactionPostingService.class);
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
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(account);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(account);
        when(paymentRepository.insert(any(Payment.class),eq(2L),eq(2L))).thenReturn(1L);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        Payment payment = paymentService.createPayment("1",paymentDto);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).insert(captor.capture(),eq(2L), eq(2L));
        assertEquals(BigDecimal.TEN, captor.getValue().getAmount());
        assertNotNull(payment);
    }

    @Test(expected = HaltException.class)
    public void createPayment_fromAccountNotFound_halt() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.TEN)
                .build();
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(null);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(account);
        when(paymentRepository.insert(any(Payment.class),eq(2L),eq(2L))).thenReturn(1L);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        paymentService.createPayment("1",paymentDto);

        verifyZeroInteractions(paymentRepository);
    }

    @Test(expected = HaltException.class)
    public void createPayment_fromAccountNotBelongToCustomer_halt() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.TEN)
                .build();
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(account);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(account);
        when(paymentRepository.insert(any(Payment.class),eq(2L),eq(2L))).thenReturn(1L);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        paymentService.createPayment("2",paymentDto);

        verifyZeroInteractions(paymentRepository);
    }

    @Test(expected = HaltException.class)
    public void createPayment_toAccountIsNull_halt() {
        PaymentDto paymentDto = PaymentDto.builder()
                .fromAccount("FROM")
                .toAccount("TO")
                .amount(BigDecimal.TEN)
                .build();
        Account account = Account.builder().id(2L).customerId(1L).build();
        when(accountService.getAccountByName(paymentDto.getFromAccount())).thenReturn(account);
        when(accountService.getAccountByName(paymentDto.getToAccount())).thenReturn(null);
        when(paymentRepository.insert(any(Payment.class),eq(2L),eq(2L))).thenReturn(1L);
        when(paymentRepository.getPayment(eq(1L), any())).thenReturn(new Payment());

        paymentService.createPayment("1",paymentDto);

        verifyZeroInteractions(paymentRepository);
    }

    @Test
    public void executePayment() {
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

        Payment returned = paymentService.executePayment("1","2");

        assertNotNull(returned);
        verify(accountService).selectForUpdate(2L);
        verify(accountService).selectForUpdate(3L);
        verify(transactionService).insertTransaction(2L);
        verify(transactionPostingService).insertTransactionPosting(3L, 3L, BigDecimal.ONE, BigDecimal.ZERO);
        verify(transactionPostingService).insertTransactionPosting(3l, 2L, BigDecimal.ZERO, BigDecimal.ONE);
        verify(accountService).updateBalance(2L, BigDecimal.TEN.subtract(BigDecimal.ONE));
        verify(accountService).updateBalance(3L, BigDecimal.TEN.add(BigDecimal.ONE));
        verify(paymentRepository).updateStatus(2L, PaymentStatus.SUCCESS, null);
    }

    @Test(expected = HaltException.class)
    public void executePayment_paymentNotFound_halt() {
        when(paymentRepository.getPayment(1L, 2L)).thenReturn(null);

        paymentService.executePayment("1","2");

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

        paymentService.executePayment("1","2");

        verifyZeroInteractions(accountService);
        verifyZeroInteractions(transactionService);
        verifyZeroInteractions(transactionPostingService);
    }

    @Test(expected = HaltException.class)
    public void executePayment_insufficientAccountBalance_halt() {
        Payment payment = Payment.builder()
                .id(1L)
                .status(PaymentStatus.PENDING)
                .creditorAccountName("2")
                .debtorAccountName("3")
                .amount(BigDecimal.valueOf(1001,2))
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

        paymentService.executePayment("1","2");

        verifyZeroInteractions(transactionService);
        verifyZeroInteractions(transactionPostingService);
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