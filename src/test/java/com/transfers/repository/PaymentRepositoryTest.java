package com.transfers.repository;

import com.transfers.domain.Payment;
import com.transfers.domain.enums.PaymentMethod;
import com.transfers.domain.enums.PaymentStatus;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PaymentRepositoryTest extends BasicRepositoryTest {
    @Test
    public void getPayments_customerId_returns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            PaymentRepository paymentRepository = session.getMapper(PaymentRepository.class);

            List<Payment> payments = paymentRepository.getPayments(-1L);

            assertNotNull(payments);
            assertEquals(3, payments.size());
            assertEquals("LT477000000000001", payments.get(0).getDebtorAccountName());
            assertEquals("LT477000000000002", payments.get(0).getCreditorAccountName());
        }
    }

    @Test
    public void getPayments_invalidId_empty() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            PaymentRepository paymentRepository = session.getMapper(PaymentRepository.class);

            List<Payment> payments = paymentRepository.getPayments(-5L);

            assertNotNull(payments);
            assertTrue(payments.isEmpty());
        }
    }

    @Test
    public void getPayment_paymentId_returns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            PaymentRepository paymentRepository = session.getMapper(PaymentRepository.class);

            Payment payment = paymentRepository.getPayment(-1L, -1L);

            assertNotNull(payment);
            assertEquals("LT477000000000002", payment.getDebtorAccountName());
            assertEquals("LT477000000000001", payment.getCreditorAccountName());
            assertNotNull(payment.getTransaction());
            assertEquals(-1L, (long)payment.getTransaction().getId());
        }
    }

    @Test
    public void getPayment_invalidId_empty() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            PaymentRepository paymentRepository = session.getMapper(PaymentRepository.class);

            Payment payment = paymentRepository.getPayment(-1L, -5L);

            assertNull(payment);
        }
    }

    @Test
    public void insert_payment_inserted() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            PaymentRepository paymentRepository = session.getMapper(PaymentRepository.class);
            Payment payment = Payment.builder()
                    .method(PaymentMethod.INTERNAL)
                    .amount(BigDecimal.TEN)
                    .message("Some msg")
                    .status(PaymentStatus.PENDING)
                    .customerId(-1L)
                    .build();
            paymentRepository.insert(payment, -1L, -2L);

            assertNotNull(payment.getId());
            assertNotNull(paymentRepository.getPayment(-1L, payment.getId()));
        }
    }

    @Test
    public void updateStatus_paymentStatus_updated() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            PaymentRepository paymentRepository = session.getMapper(PaymentRepository.class);

            paymentRepository.updateStatus(-1L,PaymentStatus.ERROR, "Bad payment");

            Payment payment = paymentRepository.getPayment(-1L, -1L);
            assertEquals(PaymentStatus.ERROR, payment.getStatus());
            assertEquals("Bad payment", payment.getErrorMessage());
        }
    }
}