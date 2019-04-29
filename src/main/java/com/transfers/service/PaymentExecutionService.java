package com.transfers.service;

import com.google.inject.Inject;
import com.transfers.api.dto.PaymentDto;
import com.transfers.domain.Account;
import com.transfers.domain.Payment;
import com.transfers.domain.enums.PaymentMethod;
import com.transfers.domain.enums.PaymentStatus;
import com.transfers.repository.PaymentRepository;
import org.mybatis.guice.transactional.Transactional;

import java.math.BigDecimal;

import static com.transfers.TransferApplication.ERROR_RESPONSE;
import static spark.Spark.halt;

public class PaymentExecutionService {
    @Inject
    private PaymentRepository paymentRepository;
    @Inject
    private AccountService accountService;
    @Inject
    private TransactionService transactionService;
    @Inject
    private TransactionPostingService transactionPostingService;

    @Transactional
    public void validateAndExecutePayment(Long customerId, Long paymentId) {
        Payment payment = paymentRepository.getPayment(customerId, paymentId);
        if (payment == null) {
            halt(404, String.format(ERROR_RESPONSE, "Payment not found or do not belong to customer"));
        }

        if (!payment.getStatus().equals(PaymentStatus.PENDING)) {
            halt(422, String.format(ERROR_RESPONSE, "Only pending payment can be executed, " +
                    "this payment's status is " + payment.getStatus()));
        }

        Account creditorAccount = accountService.getAccountByName(payment.getCreditorAccountName());
        Account debtorAccount = accountService.getAccountByName(payment.getDebtorAccountName());
        if (creditorAccount.getTotalBalance().compareTo(payment.getAmount()) < 0) {
            halt(422, String.format(ERROR_RESPONSE, "Account balance is not sufficient to execute payment"));
        }

        Long transactionId = transactionService.insertTransaction(paymentId);
        transactionPostingService.insertTransactionPosting(transactionId, debtorAccount.getId(), payment.getAmount(), BigDecimal.ZERO);
        transactionPostingService.insertTransactionPosting(transactionId, creditorAccount.getId(), BigDecimal.ZERO, payment.getAmount());
        accountService.updateBalance(creditorAccount.getId(), creditorAccount.getTotalBalance().subtract(payment.getAmount()));
        accountService.updateBalance(debtorAccount.getId(), debtorAccount.getTotalBalance().add(payment.getAmount()));
        paymentRepository.updateStatus(paymentId, PaymentStatus.SUCCESS, null);
    }

    @Transactional
    public Long validateAndCreatePayment(Long customerId, PaymentDto paymentDto) {
        Account fromAccount = accountService.getAccountByName(paymentDto.getFromAccount());
        if (fromAccount == null) {
            halt(422, String.format(ERROR_RESPONSE, "fromAccount not found"));
        }

        if (!fromAccount.getCustomerId().equals(customerId)) {
            halt(451, String.format(ERROR_RESPONSE, "From account do not belong to user"));
        }

        Account toAccount = accountService.getAccountByName(paymentDto.getToAccount());
        if (toAccount == null) {
            halt(422, String.format(ERROR_RESPONSE, "toAccount not found"));
        }

        PaymentMethod paymentMethod = toAccount.getCustomerId().equals(customerId) ?
                PaymentMethod.INTERNAL : PaymentMethod.SEPA;
        Payment payment = Payment.builder()
                .method(paymentMethod)
                .amount(paymentDto.getAmount())
                .message(paymentDto.getMessage())
                .status(PaymentStatus.PENDING)
                .customerId(customerId)
                .build();
        paymentRepository.insert(payment, fromAccount.getId(), toAccount.getId());
        return payment.getId();
    }
}
