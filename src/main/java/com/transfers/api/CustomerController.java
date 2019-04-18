package com.transfers.api;

import com.google.inject.Inject;
import com.transfers.domain.Customer;
import com.transfers.service.CustomerService;
import spark.Service;

public class CustomerController extends BaseController<Customer> {
    @Inject
    private CustomerService customerService;

    @Override
    public void configure(Service spark) {
        spark.get("/customers", (req, res) -> {
            return dataToJson(customerService.getCustomers());
        });
        spark.get("/customers/:customerId", (req, res) -> {
            return dataToJson(customerService.getCustomer(req.params("customerId")));
        });
        spark.post("/customers", (req, res) -> {
            return "";
        });
        spark.put("/customers/:customerId", (req, res) -> {
            return "";
        });
        spark.delete("/customers/:customerId", (req, res) -> {
            return "";
        });
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
