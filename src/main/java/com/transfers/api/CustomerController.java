package com.transfers.api;

import com.transfers.domain.Customer;
import com.transfers.service.CustomerService;
import spark.Service;

public class CustomerController extends BaseController<Customer> {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

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
