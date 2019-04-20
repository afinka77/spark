package com.transfers.repository;

import com.transfers.domain.Payment;
import com.transfers.domain.Transaction;
import com.transfers.domain.enums.PaymentMethod;
import com.transfers.domain.enums.PaymentStatus;
import org.apache.ibatis.session.SqlSession;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Ignore
public class TransactionRepositoryTest extends BasicRepositoryTest {
    @Test
    public void insert_paymentId_valueInserted() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            PaymentRepository paymentRepository = session.getMapper(PaymentRepository.class);
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);
            Payment payment = Payment.builder()
                    .method(PaymentMethod.INTERNAL)
                    .amount(BigDecimal.TEN)
                    .message("Some msg")
                    .status(PaymentStatus.PENDING)
                    .customerId(-1L)
                    .build();
            Long paymentId = paymentRepository.insert(payment,-1L,-2L);

            Long transactionId = transactionRepository.insert(paymentId);

            assertNotNull(transactionId);
            assertNotNull(transactionRepository.getTransactionByPaymentId(paymentId));
        }
    }

    @Test
    public void getTransactionByPaymentId_validPaymentId_valueFound() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);

            Transaction transaction = transactionRepository.getTransactionByPaymentId(-1L);

            assertNotNull(transaction);
        }
    }

    @Test
    public void getTransactionByPaymentId_invalidPaymentId_valueNotFound() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);

            Transaction transaction = transactionRepository.getTransactionByPaymentId(-5L);

            assertNull(transaction);
        }
    }
}