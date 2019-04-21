package com.transfers.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.api.dto.CustomerDto;
import com.transfers.domain.Customer;
import com.transfers.repository.CustomerRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import spark.HaltException;

import javax.inject.Provider;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomerServiceTest {
    private CustomerRepository customerRepository = mock(CustomerRepository.class);
    private Injector injector = getInjector();
    private CustomerService customerService = injector.getInstance(CustomerService.class);

    @Test
    public void getCustomers_call_customersList() {
        Customer customer = Customer.builder().id(1L).name("ABC").build();
        List<Customer> expectedCustomerList = Collections.singletonList(customer);
        when(customerRepository.getCustomers()).thenReturn(expectedCustomerList);

        List<Customer> customerList = customerService.getCustomers();

        assertEquals(expectedCustomerList, customerList);
    }

    @Test
    public void getCustomer_customerId_returns() {
        Customer expected = Customer.builder().id(1L).name("ABC").build();
        when(customerRepository.getCustomer(1L)).thenReturn(expected);

        Customer customer = customerService.getCustomer("1");

        assertEquals(expected, customer);
    }

    @Test
    public void insertCustomer_customerDto_inserted() {
        CustomerDto customerDto = CustomerDto.builder().name("Dainius").build();

        customerService.insertCustomer(customerDto);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).insert(captor.capture());
        assertEquals("Dainius", captor.getValue().getName());
    }

    @Test
    public void updateCustomer_customerId_updated() {
        CustomerDto customerDto = CustomerDto.builder().name("Dainius").build();
        Customer expected = Customer.builder().id(1L).name("Dainius").build();
        when(customerRepository.getCustomer(1L)).thenReturn(expected);

        Customer customer = customerService.updateCustomer("1", customerDto);

        assertEquals(expected, customer);
        verify(customerRepository).update(expected);
    }

    @Test(expected = HaltException.class)
    public void updateCustomer_invalidCustomerId_halt() {
        CustomerDto customerDto = CustomerDto.builder().name("Dainius").build();

        customerService.updateCustomer("1", customerDto);

        verify(customerRepository, times(0)).update(any());
    }

    @Test
    public void deleteCustomer_customerId_deleted() {
        Customer expected = Customer.builder().id(1L).name("Dainius").build();
        when(customerRepository.getCustomer(1L)).thenReturn(expected);

        customerService.deleteCustomer("1");

        verify(customerRepository).delete(1L);
    }

    @Test(expected = HaltException.class)
    public void deleteCustomer_invalidCustomerId_halt() {
        customerService.deleteCustomer("1");

        verify(customerRepository, times(0)).delete(anyLong());
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CustomerService.class);
                bind(CustomerRepository.class).toProvider(new Provider<CustomerRepository>() {
                    public CustomerRepository get() {
                        return customerRepository;
                    }
                });
            }
        });
    }
}