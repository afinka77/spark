package com.transfers.service;

import com.google.inject.Inject;
import com.transfers.domain.Customer;
import com.transfers.repository.CustomerRepository;
import org.mybatis.guice.transactional.Transactional;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class CustomerService {
    @Inject
    private CustomerRepository customerRepository;

    @Transactional
    public List<Customer> getCustomers() {
        return customerRepository.getCustomers();
    }

    @Transactional
    public Customer getCustomer(String customerId) {
        return customerRepository.getCustomer(Long.valueOf(customerId));
    }
}
