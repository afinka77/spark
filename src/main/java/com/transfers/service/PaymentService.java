package com.transfers.service;

import com.google.inject.Inject;
import com.transfers.api.dto.PaymentDto;
import com.transfers.domain.Payment;
import com.transfers.repository.PaymentRepository;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static com.transfers.TransferApplication.ERROR_RESPONSE;
import static spark.Spark.halt;

@Singleton
public class PaymentService {
    @Inject
    private PaymentRepository paymentRepository;
    @Inject
    private PaymentExecutionService paymentExecutionService;

    private ReentrantLock reentrantLock = new ReentrantLock(true);

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

    public Payment createPayment(String pCustomerId, PaymentDto paymentDto) {
        try {
            reentrantLock.lock();
            Long customerId = Long.valueOf(pCustomerId);
            Long paymentId = paymentExecutionService.validateAndCreatePayment(customerId, paymentDto);
            return paymentRepository.getPayment(customerId, paymentId);
        } catch (Exception e) {
            e.printStackTrace();
            halt(500, String.format(ERROR_RESPONSE, e.getMessage()));
        } finally {
            if (reentrantLock.isHeldByCurrentThread()) {
                reentrantLock.unlock();
            }
        }

        return null;
    }

    public Payment executePayment(String pCustomerId, String pPaymentId) {
        try {
            reentrantLock.lock();
            Long customerId = Long.valueOf(pCustomerId);
            Long paymentId = Long.valueOf(pPaymentId);
            paymentExecutionService.validateAndExecutePayment(customerId, paymentId);
            return paymentRepository.getPayment(customerId, paymentId);
        } catch (Exception e) {
            e.printStackTrace();
            halt(500, String.format(ERROR_RESPONSE, e.getMessage()));
        } finally {
            if (reentrantLock.isHeldByCurrentThread()) {
                reentrantLock.unlock();
            }
        }

        return null;
    }
}
