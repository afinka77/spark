package com.transfers.repository;

import com.transfers.domain.Account;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AccountRepositoryTest extends BasicRepositoryTest {
    @Test
    public void getAccounts_customerId_returns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            List<Account> accounts = accountRepository.getAccounts(-1L);

            assertEquals(1, accounts.size());
            assertEquals(2, accounts.get(0).getTransactionPostings().size());
        }
    }

    @Test
    public void getAccounts_invalidId_emptyList() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            List<Account> accounts = accountRepository.getAccounts(-5L);

            assertEquals(0, accounts.size());
        }
    }

    @Test
    public void getAccountName_accountId_returns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            String accountName = accountRepository.getAccountName(-1L);

            assertEquals("LT477000000000001", accountName);
        }
    }

    @Test
    public void getAccountName_invalidId_null() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            String accountName = accountRepository.getAccountName(-5L);

            assertNull(accountName);
        }
    }

    @Test
    public void getAccountByName_accountName_returns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);
            String accountName = "LT477000000000001";

            Account account = accountRepository.getAccountByName(accountName);

            assertNotNull(account);
            assertEquals(accountName, account.getName());
        }
    }

    @Test
    public void getAccountByName_invalidName_notReturns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            Account account = accountRepository.getAccountByName("invalidName");

            assertNull(account);
        }
    }

    @Test
    public void selectForUpdate_accountId_returns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            Account account = accountRepository.selectForUpdate(-1L);

            assertNotNull(account);
        }
    }

    @Test
    public void selectForUpdate_invalidId_null() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            Account account = accountRepository.selectForUpdate(-5L);

            assertNull(account);
        }
    }

    @Test
    public void selectAccount_accountId_returns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            Account account = accountRepository.selectAccount(-1L);

            assertNotNull(account);
        }
    }

    @Test
    public void selectAccount_invalidId_null() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            Account account = accountRepository.selectAccount(-5L);

            assertNull(account);
        }
    }

    @Test
    public void updateBalance_balance_updated() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);
            BigDecimal newBalance =  BigDecimal.valueOf(2345,2);
            accountRepository.updateBalance(-1L, newBalance);

            Account account = accountRepository.selectAccount(-1L);
            assertEquals(newBalance, account.getTotalBalance());
        }
    }

    @Test
    public void insert_acount_inserted() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);
            String accountName = "someNewName";
            Account account = Account.builder()
                    .name(accountName)
                    .customerId(-1L)
                    .build();

            accountRepository.insert(account);

            Account inserted = accountRepository.getAccountByName(accountName);
            assertNotNull(inserted);
            assertEquals(accountName, inserted.getName());
        }
    }

    @Test
    public void update_acount_updated() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);
            String accountName = "someNewName";
            Long newCustomerId = -2L;
            Account account = Account.builder()
                    .id(-1L)
                    .name(accountName)
                    .customerId(newCustomerId)
                    .build();

            accountRepository.update(account);

            Account updated = accountRepository.selectAccount(-1L);
            assertNotNull(updated);
            assertEquals(accountName, updated.getName());
            assertEquals(newCustomerId, updated.getCustomerId());
        }
    }

    @Test
    public void delete_acount_deleted() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountRepository accountRepository = session.getMapper(AccountRepository.class);

            accountRepository.delete(-1L);

            assertNull(accountRepository.selectAccount(-1L));
        }
    }
}