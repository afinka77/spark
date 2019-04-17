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
    }

    /*
    public static void main(String[] args) {

        get("/hello", (req, res)->"Hello, world");

        get("/hello/:name", (req,res)->{
            return "Hello, "+ req.params(":name");
        });
    }*/
}
