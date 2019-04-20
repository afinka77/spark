package com.transfers.api;

import com.google.inject.Inject;
import com.transfers.api.dto.CustomerDto;
import com.transfers.domain.Customer;
import com.transfers.service.CustomerService;
import spark.Service;

public class CustomerController extends BaseController<CustomerDto> {
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
            Customer customer = customerService.insertCustomer(
                    jsonToData(req.body(), CustomerDto.class));
            return dataToJson(customer);
        });

        spark.put("/customers/:customerId", (req, res) -> {
            Customer customer = customerService.updateCustomer(req.params("customerId"),
                    jsonToData(req.body(), CustomerDto.class));
            return dataToJson(customer);
        });

        spark.delete("/customers/:customerId", (req, res) -> {
            customerService.deleteCustomer(req.params("customerId"));
            return "";
        });
    }
}
