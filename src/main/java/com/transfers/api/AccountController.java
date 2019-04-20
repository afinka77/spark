package com.transfers.api;

import com.google.inject.Inject;
import com.transfers.api.dto.AccountDto;
import com.transfers.domain.Account;
import com.transfers.service.AccountService;
import spark.Service;

public class AccountController extends BaseController<AccountDto> {
    @Inject
    private AccountService accountService;

    @Override
    public void configure(Service spark) {
        spark.post("/customers/:customerId/accounts", (req, res) -> {
            Account account = accountService.insertAccount(req.params("customerId"),
                    jsonToData(req.body(), AccountDto.class));
            return dataToJson(account);
        });

        spark.put("/customers/:customerId/accounts/:accountId", (req, res) -> {
            Account account = accountService.updateAccount(req.params("customerId"),
                    req.params("accountId"),
                    jsonToData(req.body(), AccountDto.class));
            return dataToJson(account);
        });

        spark.delete("/customers/:customerId/accounts/:accountId", (req, res) -> {
            accountService.deleteAccount(req.params("customerId"), req.params("accountId"));
            return "";
        });
    }
}