package com.transfers.api;

import com.transfers.domain.Account;
import com.transfers.service.AccountService;
import spark.Service;

public class AccountController extends BaseController<Account> {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void configure(Service spark) {
        spark.get("/accounts", (req, res) -> {
            return dataToJson(accountService.getAccounts());
        });
    }
}
