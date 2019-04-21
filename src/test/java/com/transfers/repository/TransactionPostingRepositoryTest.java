package com.transfers.repository;

import com.transfers.domain.TransactionPosting;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransactionPostingRepositoryTest extends BasicRepositoryTest {
    @Test
    public void insert_transactionPosting_inserted() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TransactionPostingRepository transactionPostingRepository = session.getMapper(TransactionPostingRepository.class);
            Long transactionId = -1L;
            TransactionPosting transactionPosting = TransactionPosting.builder()
                    .transactionId(transactionId)
                    .accountId(-1L)
                    .debit(BigDecimal.ONE)
                    .credit(BigDecimal.ZERO)
                    .build();

            Long id = transactionPostingRepository.insert(transactionPosting);

            assertNotNull(id);
            assertEquals(1, transactionPostingRepository.getTransactionPostings(-1L)
                    .stream()
                    .filter(t -> t.getId().equals(id))
                    .toArray()
                    .length);
        }
    }

    @Test
    public void getTransactionPostings_call_returned() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TransactionPostingRepository transactionPostingRepository = session.getMapper(TransactionPostingRepository.class);

            List<TransactionPosting> transactionPostings = transactionPostingRepository.getTransactionPostings(-1L);

            assertEquals(2, transactionPostings.size());
        }
    }

    @Test
    public void getTransactionPostingsByAccount_validAccountId_returned() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TransactionPostingRepository transactionPostingRepository = session.getMapper(TransactionPostingRepository.class);

            List<TransactionPosting> transactionPostings = transactionPostingRepository.getTransactionPostingsByAccount(-1L);

            assertEquals(2, transactionPostings.size());
        }
    }

    @Test
    public void getTransactionPostingsByAccount_inValidAccountId_notReturned() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TransactionPostingRepository transactionPostingRepository = session.getMapper(TransactionPostingRepository.class);

            List<TransactionPosting> transactionPostings = transactionPostingRepository.getTransactionPostingsByAccount(-7L);

            assertEquals(0, transactionPostings.size());
        }
    }
}