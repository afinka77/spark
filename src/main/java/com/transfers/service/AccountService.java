package com.transfers.service;

import com.google.inject.Inject;
import com.transfers.api.dto.AccountDto;
import com.transfers.domain.Account;
import com.transfers.domain.Customer;
import com.transfers.repository.AccountRepository;
import org.apache.ibatis.annotations.Insert;
import org.mybatis.guice.transactional.Transactional;

import javax.inject.Singleton;
import java.math.BigDecimal;

import static com.transfers.TransferApplication.ERROR_RESPONSE;
import static spark.Spark.halt;

@Singleton
public class AccountService {
    @Inject
    private AccountRepository accountRepository;
    @Inject
    private CustomerService customerService;

    public Account getAccountByName(String accountName) {
        return accountRepository.getAccountByName(accountName);
    }

    public void selectForUpdate(Long accountId) {
        accountRepository.selectForUpdate(accountId);
    }

    public void updateBalance(Long accountId, BigDecimal balance) {
        accountRepository.updateBalance(accountId, balance);
    }

    @Transactional
    public Account insertAccount(String customerId, AccountDto accountDto) {
        Customer selected = customerService.getCustomer(customerId);
        if (selected == null) {
            halt(422, String.format(ERROR_RESPONSE, "Customer not found"));
        }
        Account account = Account.builder()
                .name(accountDto.getName())
                .customerId(Long.valueOf(customerId))
                .build();
        Long accountId = accountRepository.insert(account);
        return accountRepository.selectAccount(accountId);
    }

    @Transactional
    public Account updateAccount(String pCustomerId, String pAccountId, AccountDto accountDto) {
        Long customerId = Long.valueOf(pCustomerId);
        Long accountId = Long.valueOf(pAccountId);
        validateAccountId(customerId, accountId);
        Account account = Account.builder()
                .id(accountId)
                .name(accountDto.getName())
                .customerId(Long.valueOf(customerId))
                .build();
        accountRepository.update(account);
        return accountRepository.selectAccount(accountId);
    }

    @Transactional
    public void deleteAccount(String pCustomerId, String pAccountId) {
        Long customerId = Long.valueOf(pCustomerId);
        Long accountId = Long.valueOf(pAccountId);
        validateAccountId(customerId, accountId);
        accountRepository.delete(accountId);
    }

    private void validateAccountId(Long customerId, Long accountId) {
        Account selected = accountRepository.selectAccount(accountId);
        if (selected == null) {
            halt(422, String.format(ERROR_RESPONSE, "Account not found"));
        }
        if (!selected.getCustomerId().equals(customerId)) {
            halt(422, String.format(ERROR_RESPONSE, "Account don't belong to user"));
        }
    }
}
