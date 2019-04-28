package com.transfers.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.api.dto.AccountDto;
import com.transfers.domain.Account;
import com.transfers.domain.Customer;
import com.transfers.repository.AccountRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import spark.HaltException;

import javax.inject.Provider;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class AccountServiceTest {
    private AccountRepository accountRepository = mock(AccountRepository.class);
    private CustomerService customerService = mock(CustomerService.class);
    private Injector injector = getInjector();
    private AccountService accountService = injector.getInstance(AccountService.class);

    @Test
    public void getAccountByName_name_returned() {
        String accountName = "Account123";
        Account expected = Account.builder().name(accountName).build();
        when(accountRepository.getAccountByName(accountName)).thenReturn(expected);

        Account account = accountService.getAccountByName(accountName);

        assertEquals(expected, account);
    }

    @Test
    public void updateBalance_accountIdAndAmount_called() {
        Long accountId = 125L;
        BigDecimal amount = BigDecimal.valueOf(1234, 2);

        accountService.updateBalance(accountId, amount);

        verify(accountRepository).updateBalance(accountId, amount);
    }

    @Test
    public void insertAccount_validCustomerId_inserted() {
        String accountName = "Account123";
        String customerId = "12";
        AccountDto accountDto = AccountDto.builder().name(accountName).build();
        when(customerService.getCustomer(customerId)).thenReturn(new Customer());

        accountService.insertAccount(customerId, accountDto);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());
        assertEquals("Account123", captor.getValue().getName());
    }

    @Test(expected = HaltException.class)
    public void insertAccount_invalidCustomerId_halt() {
        String accountName = "Account123";
        String customerId = "12";
        AccountDto accountDto = AccountDto.builder().name(accountName).build();
        accountService.insertAccount(customerId, accountDto);

        verifyZeroInteractions(accountRepository);
    }

    @Test
    public void updateAccount_validIds_updated() {
        String accountName = "Account123";
        String customerId = "12";
        String accountId = "23";
        AccountDto accountDto = AccountDto.builder().name(accountName).build();
        when(customerService.getCustomer(customerId)).thenReturn(new Customer());
        Account expected = Account.builder()
                .id(23L)
                .name(accountName)
                .customerId(12L)
                .build();
        when(accountRepository.selectAccount(anyLong())).thenReturn(expected);

        Account account = accountService.updateAccount(customerId, accountId, accountDto);

        assertEquals(expected, account);
    }

    @Test(expected = HaltException.class)
    public void updateAccount_customerNotFound_halt() {
        String accountName = "Account123";
        String customerId = "12";
        String accountId = "23";
        AccountDto accountDto = AccountDto.builder().name(accountName).build();

        accountService.updateAccount(customerId, accountId, accountDto);

        verifyZeroInteractions(accountRepository);
    }

    @Test(expected = HaltException.class)
    public void updateAccount_accountNotFound_halt() {
        String accountName = "Account123";
        String customerId = "12";
        String accountId = "23";
        AccountDto accountDto = AccountDto.builder().name(accountName).build();
        when(customerService.getCustomer(customerId)).thenReturn(new Customer());

        accountService.updateAccount(customerId, accountId, accountDto);

        verifyZeroInteractions(accountRepository);
    }

    @Test
    public void deleteAccount_validIds_deleted() {
        String accountName = "Account123";
        String customerId = "12";
        String accountId = "23";
        when(customerService.getCustomer(customerId)).thenReturn(new Customer());
        Account expected = Account.builder()
                .id(23L)
                .name(accountName)
                .customerId(12L)
                .build();
        when(accountRepository.selectAccount(anyLong())).thenReturn(expected);

        accountService.deleteAccount(customerId, accountId);

        verify(accountRepository).delete(23L);
    }

    @Test(expected = HaltException.class)
    public void deleteAccount_customerNotFound_halt() {
        String customerId = "12";
        String accountId = "23";
        accountService.deleteAccount(customerId, accountId);

        verifyZeroInteractions(accountRepository);
    }

    @Test(expected = HaltException.class)
    public void deleteAccount_accountNotFound_halt() {
        String customerId = "12";
        String accountId = "23";
        when(customerService.getCustomer(customerId)).thenReturn(new Customer());

        accountService.deleteAccount(customerId, accountId);

        verifyZeroInteractions(accountRepository);
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(AccountService.class);
                bind(AccountRepository.class).toProvider(new Provider<AccountRepository>() {
                    public AccountRepository get() {
                        return accountRepository;
                    }
                });
                bind(CustomerService.class).toProvider(new Provider<CustomerService>() {
                    public CustomerService get() {
                        return customerService;
                    }
                });
            }
        });
    }
}