package com.transfers.repository;

import com.transfers.domain.Transaction;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TransactionRepositoryTest extends BasicRepositoryTest {
    @Test
    public void insert_paymentId_valueInserted() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);

            Long transactionId = transactionRepository.insert(-3L);

            assertNotNull(transactionId);
            assertNotNull(transactionRepository.getTransactionByPaymentId(-3L));
        }
    }

    @Test
    public void getTransactionByPaymentId_validPaymentId_valueFound() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TransactionRepository transactionRepository = session.getMapper(TransactionRepository.class);

            Transaction transaction = transactionRepository.getTransactionByPaymentId(-1L);

            assertNotNull(transaction);
            assertEquals(2, transaction.getTransactionPostings().size());
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