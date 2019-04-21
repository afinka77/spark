package com.transfers.service;

import com.google.inject.Inject;
import com.transfers.api.dto.CustomerDto;
import com.transfers.domain.Customer;
import com.transfers.repository.CustomerRepository;
import org.mybatis.guice.transactional.Transactional;

import javax.inject.Singleton;
import java.util.List;

import static com.transfers.TransferApplication.ERROR_RESPONSE;
import static spark.Spark.halt;

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

    @Transactional
    public Customer insertCustomer(CustomerDto customerDto) {
        Customer customer = Customer.builder()
                .name(customerDto.getName())
                .build();
        Long customerId = customerRepository.insert(customer);
        return customerRepository.getCustomer(customerId);
    }

    @Transactional
    public Customer updateCustomer(String pCustomerId, CustomerDto customerDto) {
        Long customerId = Long.valueOf(pCustomerId);
        validateCustomerId(customerId);
        Customer customer = Customer.builder()
                .id(customerId)
                .name(customerDto.getName())
                .build();
        customerRepository.update(customer);
        return customerRepository.getCustomer(customerId);
    }

    @Transactional
    public void deleteCustomer(String pCustomerId) {
        Long customerId = Long.valueOf(pCustomerId);
        validateCustomerId(customerId);
        customerRepository.delete(customerId);
    }

    private void validateCustomerId(Long customerId) {
        Customer selected = customerRepository.getCustomer(customerId);
        if (selected == null) {
            halt(422, String.format(ERROR_RESPONSE, "Customer not found"));
        }
    }
}
