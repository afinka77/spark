package com.transfers.service;

import com.google.inject.Inject;
import com.transfers.domain.Account;
import com.transfers.repository.AccountRepository;
import org.mybatis.guice.transactional.Transactional;

import java.util.List;

public class AccountService {
    @Inject
    private AccountRepository accountRepository;

    @Transactional
    public List<Account> getAccounts() {
        return accountRepository.getAccounts();
    }
}
