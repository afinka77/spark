package com.transfers.repository;

import com.transfers.domain.Customer;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CustomerRepositoryTest extends BasicRepositoryTest {
    @Test
    public void getCustomers_call_returns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CustomerRepository customerRepository = session.getMapper(CustomerRepository.class);

            List<Customer> customers = customerRepository.getCustomers();

            assertEquals(2, customers.size());
        }
    }

    @Test
    public void getCustomer_customerId_returns() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CustomerRepository customerRepository = session.getMapper(CustomerRepository.class);

            Customer customer = customerRepository.getCustomer(-1L);

            assertNotNull(customer);
            assertEquals(1,customer.getAccounts().size());
        }
    }

    @Test
    public void getCustomer_invalidId_null() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CustomerRepository customerRepository = session.getMapper(CustomerRepository.class);

            Customer customer = customerRepository.getCustomer(-5L);

            assertNull(customer);
        }
    }

    @Test
    public void insert_customer_inserted() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CustomerRepository customerRepository = session.getMapper(CustomerRepository.class);
            String newName = "Vytautas Disysis";
            Customer customer = Customer.builder()
                    .name(newName)
                    .build();

            Long id = customerRepository.insert(customer);

            Customer inserted = customerRepository.getCustomer(id);
            assertNotNull(inserted);
            assertEquals(newName, inserted.getName());
        }
    }

    @Test
    public void update_customer_updated() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CustomerRepository customerRepository = session.getMapper(CustomerRepository.class);
            String newName = "Vytautas Disysis";
            Customer customer = Customer.builder()
                    .id(-1L)
                    .name(newName)
                    .build();

            customerRepository.update(customer);

            Customer updated = customerRepository.getCustomer(-1L);
            assertEquals(newName, updated.getName());
        }
    }

    @Test
    public void delete_customerId_deleted() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CustomerRepository customerRepository = session.getMapper(CustomerRepository.class);

            customerRepository.delete(-1L);

            assertNull(customerRepository.getCustomer(-1L));
        }
    }
}