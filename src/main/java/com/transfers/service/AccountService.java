package com.transfers.service;

import com.transfers.domain.Account;
import com.transfers.repository.AccountRepository;

import java.util.List;


public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccounts() {
        return accountRepository.getAccounts();
    }
}
