package com.transfers.service;

import com.transfers.domain.Customer;
import com.transfers.repository.CustomerRepository;

import java.util.List;


public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers() {
        return customerRepository.getCustomers();
    }
}
