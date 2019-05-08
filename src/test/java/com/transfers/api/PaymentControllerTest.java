package com.transfers.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.api.dto.PaymentDto;
import com.transfers.domain.Customer;
import com.transfers.domain.Payment;
import com.transfers.domain.enums.PaymentStatus;
import com.transfers.service.PaymentService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Service;

import javax.inject.Provider;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentControllerTest extends BasicControllerTest {
    private static final int PORT = 8752;
    private static final String URL = "http://localhost:" + PORT;
    private Service spark;
    private HttpClient client = HttpClient.newHttpClient();

    private PaymentService paymentService = mock(PaymentService.class);

    @Before
    public void setUp() {
        spark = Service.ignite().port(PORT);
        getInjector().getInstance(PaymentController.class).configure(spark);
        spark.awaitInitialization();
    }

    @After
    public void tearDown() {
        spark.stop();
        spark.awaitStop();
    }

    @Test
    public void test() throws Exception {
        get_customerId_paymentsList();
        get_paymentId_payment();
        post_paymentDto_createPayment();
        put_paymentId_executePayment();
    }

    private void get_customerId_paymentsList() throws Exception {
        Payment payment = Payment.builder()
                .id(1L)
                .amount(BigDecimal.ONE)
                .status(PaymentStatus.PENDING)
                .build();
        List<Payment> expected = Collections.singletonList(payment);
        when(paymentService.getPayments("1")).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/1/payments"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(dataToJson(expected), response.body());
    }

    private void get_paymentId_payment() throws Exception{
        Payment expected= Payment.builder()
                .id(1L)
                .amount(BigDecimal.ONE)
                .status(PaymentStatus.PENDING)
                .build();
        when(paymentService.getPayment("1","2")).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/1/payments/2"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(dataToJson(expected), response.body());
    }

    private void post_paymentDto_createPayment() throws Exception{
        PaymentDto input = PaymentDto.builder()
                .fromAccount("Acc1")
                .toAccount("Acc2")
                .amount(BigDecimal.TEN)
                .message("Skolos grazinimas")
                .build();
        Payment expected= Payment.builder()
                .id(5L)
                .amount(BigDecimal.TEN)
                .status(PaymentStatus.PENDING)
                .build();
        when(paymentService.createPayment("1",input)).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/1/payments"))
                .POST(HttpRequest.BodyPublishers.ofString(dataToJson(input)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(dataToJson(expected), response.body());
    }

    private void put_paymentId_executePayment() throws Exception {
        Payment expected= Payment.builder()
                .id(5L)
                .amount(BigDecimal.TEN)
                .status(PaymentStatus.PENDING)
                .build();
        when(paymentService.executePayment("1","2")).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/1/payments/2"))
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(dataToJson(expected), response.body());
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(PaymentController.class);
                bind(PaymentService.class).toProvider(new Provider<PaymentService>() {
                    public PaymentService get() {
                        return paymentService;
                    }
                });
            }
        });
    }
}