package com.transfers.service;

import com.google.inject.Inject;
import com.transfers.api.dto.PaymentDto;
import com.transfers.domain.Account;
import com.transfers.domain.Payment;
import com.transfers.domain.enums.PaymentMethod;
import com.transfers.domain.enums.PaymentStatus;
import com.transfers.repository.PaymentRepository;
import org.mybatis.guice.transactional.Transactional;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

import static com.transfers.TransferApplication.ERROR_RESPONSE;
import static spark.Spark.halt;

@Singleton
public class PaymentService {
    @Inject
    private PaymentRepository paymentRepository;
    @Inject
    private AccountService accountService;
    @Inject
    private TransactionService transactionService;
    @Inject
    private TransactionPostingService transactionPostingService;

    public List<Payment> getPayments(String customerId) {
        return paymentRepository.getPayments(Long.valueOf(customerId));
    }

    public Payment getPayment(String customerId, String paymentId) {
        Payment payment = paymentRepository.getPayment(Long.valueOf(customerId), Long.valueOf(paymentId));
        if (payment == null) {
            halt(404, String.format(ERROR_RESPONSE, "Payment not found or do not belong to customer"));
        }
        return payment;
    }

    @Transactional
    public Payment createPayment(String pCustomerId, PaymentDto paymentDto) {
        Long customerId = Long.valueOf(pCustomerId);
        Long paymentId = validateAndCreatePayment(customerId, paymentDto);
        return paymentRepository.getPayment(customerId, paymentId);
    }


    @Transactional
    public Payment executePayment(String pCustomerId, String pPaymentId) {
        Long customerId = Long.valueOf(pCustomerId);
        Long paymentId = Long.valueOf(pPaymentId);
        validateAndExecutePayment(customerId, paymentId);
        return paymentRepository.getPayment(customerId, paymentId);
    }

    private void validateAndExecutePayment(Long customerId, Long paymentId) {
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
        accountService.selectForUpdate(creditorAccount.getId());
        accountService.selectForUpdate(debtorAccount.getId());
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

    private Long validateAndCreatePayment(Long customerId, PaymentDto paymentDto) {
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
