package com.transfers.service;

import com.google.inject.Inject;
import com.transfers.domain.Account;
import com.transfers.repository.AccountRepository;

import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
public class AccountService {
    @Inject
    private AccountRepository accountRepository;

    public Account getAccountByName(String accountName) {
        return accountRepository.getAccountByName(accountName);
    }

    public void selectForUpdate(Long accountId){
        accountRepository.selectForUpdate(accountId);
    }

    public void updateBalance(Long accountId, BigDecimal balance){
        accountRepository.updateBalance(accountId, balance);
    }
}
