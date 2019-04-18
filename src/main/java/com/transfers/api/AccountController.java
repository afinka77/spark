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
        spark.post("/customers/:customerId/accounts", (req, res) -> {
            return "";
        });
        spark.put("/customers/:customerId/accounts/:accountId", (req, res) -> {
            return "";
        });
        spark.delete("/customers/:customerId/accounts/:accountId", (req, res) -> {
            return "";
        });
    }
}