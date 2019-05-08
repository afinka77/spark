package com.transfers.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.api.dto.CustomerDto;
import com.transfers.domain.Customer;
import com.transfers.service.CustomerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Service;

import javax.inject.Provider;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomerControllerTest extends BasicControllerTest {
    private static final int PORT = 8744;
    private static final String URL = "http://localhost:" + PORT;
    private Service spark;
    private HttpClient client = HttpClient.newHttpClient();

    private CustomerService customerService = mock(CustomerService.class);

    @Before
    public void setUp() {
        spark = Service.ignite().port(PORT);
        getInjector().getInstance(CustomerController.class).configure(spark);
        spark.awaitInitialization();
    }

    @After
    public void tearDown() {
        spark.stop();
        spark.awaitStop();
    }

    @Test
    public void customeController_endpoints_200() throws Exception {
        get_empty_customersList();
        get_customerId_customer();
        post_customerDto_customer();
        put_customerDto_customer();
        delete_customerId_200();
    }

    private void get_empty_customersList() throws Exception {
        Customer customer = Customer.builder()
                .name("Darius")
                .id(1L)
                .build();
        List<Customer> expected = Collections.singletonList(customer);
        when(customerService.getCustomers()).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(dataToJson(expected), response.body());
    }

    private void get_customerId_customer() throws Exception {
        Customer expected = Customer.builder()
                .name("Darius")
                .id(1L)
                .build();
        when(customerService.getCustomer("1")).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(dataToJson(expected), response.body());
    }

    private void post_customerDto_customer() throws Exception {
        Customer expected = Customer.builder()
                .name("Darius")
                .id(1L)
                .build();
        CustomerDto input = CustomerDto.builder()
                .name("Darius")
                .build();
        when(customerService.insertCustomer(input)).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers"))
                .POST(HttpRequest.BodyPublishers.ofString(dataToJson(input)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(dataToJson(expected), response.body());
    }

    private void put_customerDto_customer() throws Exception {
        Customer expected = Customer.builder()
                .name("Darius")
                .id(1L)
                .build();
        CustomerDto input = CustomerDto.builder()
                .name("Darius")
                .build();
        when(customerService.updateCustomer("1", input)).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/1"))
                .PUT(HttpRequest.BodyPublishers.ofString(dataToJson(input)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(dataToJson(expected), response.body());
    }

    private void delete_customerId_200() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(CustomerController.class);
                bind(CustomerService.class).toProvider(new Provider<CustomerService>() {
                    public CustomerService get() {
                        return customerService;
                    }
                });
            }
        });
    }
}