package com.transfers.api;

import com.google.inject.Inject;
import com.transfers.domain.Account;
import com.transfers.service.AccountService;
import spark.Service;

public class AccountController extends BaseController<Account> {
    @Inject
    private AccountService accountService;

    @Override
    public void configure(Service spark) {
        spark.get("/accounts", (req, res) -> {
            return dataToJson(accountService.getAccounts());
        });
    }
}
