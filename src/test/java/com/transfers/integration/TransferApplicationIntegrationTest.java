package com.transfers.integration;

import com.google.inject.Injector;
import com.transfers.api.AccountController;
import com.transfers.api.CustomerController;
import com.transfers.api.PaymentController;
import com.transfers.api.dto.AccountDto;
import com.transfers.api.dto.CustomerDto;
import com.transfers.api.dto.PaymentDto;
import com.transfers.domain.Account;
import com.transfers.domain.Customer;
import com.transfers.domain.Payment;
import com.transfers.service.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mybatis.guice.XMLMyBatisModule;
import org.mybatis.guice.datasource.helper.JdbcHelper;
import spark.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.IntStream;

import static com.google.inject.Guice.createInjector;
import static com.transfers.api.BasicControllerTest.dataToJson;
import static com.transfers.api.BasicControllerTest.jsonToData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class TransferApplicationIntegrationTest {
    private static final int PORT = 8385;
    private static final String URL = "http://localhost:" + PORT;
    private Injector injector = injector();
    private Service spark;
    private HttpClient client = HttpClient.newHttpClient();

    private CustomerController customerController;
    private AccountController accountController;
    private PaymentController paymentController;

    @Before
    public void setUp() {
        spark = Service.ignite().port(PORT);
        customerController = injector.getInstance(CustomerController.class);
        customerController.configure(spark);
        paymentController = injector.getInstance(PaymentController.class);
        paymentController.configure(spark);
        accountController = injector.getInstance(AccountController.class);
        accountController.configure(spark);
    }

    @After
    public void tearDown() {
        spark.stop();
        spark.awaitStop();
    }


    @Test
    public void make100Payments_25x1EURTo4Accounts_correctBalances() throws Exception {
        /**
         * From account, having 100 EURO balance
         */
        String fromAccountName = "LT477000000000001";
        assertEquals(BigDecimal.valueOf(10000, 2), getAccountBalance("-1"));
        BigDecimal amount = BigDecimal.valueOf(100, 2);

        /**
         * Creating 4 customers with accounts transfers will be made to
         */
        String customerId1 = createCustomer("Marius");
        String customerId1accountName = "MariusAccount";
        String customerId1accountId = createAccount(customerId1, customerId1accountName);
        assertNotNull(customerId1accountId);

        String customerId2 = createCustomer("Giedrius");
        String customerId2accountName = "GiedriusAccount";
        String customerId2accountId = createAccount(customerId2, customerId2accountName);
        assertNotNull(customerId2accountId);

        String customerId3 = createCustomer("Vida");
        String customerId3accountName = "VidaAccount";
        String customerId3accountId = createAccount(customerId3, customerId3accountName);
        assertNotNull(customerId3accountId);

        String customerId4 = createCustomer("Milda");
        String customerId4accountName = "MildaAccount";
        String customerId4accountId = createAccount(customerId4, customerId4accountName);
        assertNotNull(customerId4accountId);

        /**
         * Making 100 (25x4) payments in parallel streams
         */
        IntStream.rangeClosed(1, 25).parallel().forEach((i) -> {
            makePayment("-1", fromAccountName, customerId1accountName, amount);
            makePayment("-1", fromAccountName, customerId2accountName, amount);
            makePayment("-1", fromAccountName, customerId3accountName, amount);
            makePayment("-1", fromAccountName, customerId4accountName, amount);
        });

        /**
         * Checking balances after transfer
         */
        assertEquals(BigDecimal.valueOf(0, 2), getAccountBalance("-1"));
        assertEquals(BigDecimal.valueOf(2500, 2), getAccountBalance(customerId1));
        assertEquals(BigDecimal.valueOf(2500, 2), getAccountBalance(customerId2));
        assertEquals(BigDecimal.valueOf(2500, 2), getAccountBalance(customerId3));
        assertEquals(BigDecimal.valueOf(2500, 2), getAccountBalance(customerId3));
    }

    private Injector injector() {
        return createInjector(new XMLMyBatisModule() {
            @Override
            protected void initialize() {
                install(JdbcHelper.HSQLDB_Embedded);
                bind(CustomerService.class);
                bind(PaymentService.class);
                bind(AccountService.class);
                bind(TransactionService.class);
                bind(TransactionPostingService.class);
            }
        });
    }

    private String createCustomer(String name) throws Exception {
        CustomerDto input = CustomerDto.builder()
                .name(name)
                .build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers"))
                .POST(HttpRequest.BodyPublishers.ofString(dataToJson(input)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Customer customer = (Customer) jsonToData(response.body(), Customer.class);
        return customer.getId().toString();
    }

    private String createAccount(String customerId, String name) throws Exception {
        AccountDto input = AccountDto.builder()
                .name(name)
                .build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/" + customerId + "/accounts"))
                .POST(HttpRequest.BodyPublishers.ofString(dataToJson(input)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Account account = (Account) jsonToData(response.body(), Account.class);
        return account.getId().toString();
    }

    private void makePayment(String customerId,
                             String fromAccountName,
                             String toAccountName,
                             BigDecimal amount) {

        PaymentDto input = PaymentDto.builder()
                .fromAccount(fromAccountName)
                .toAccount(toAccountName)
                .amount(amount)
                .message("Mokejimas")
                .build();
        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/" + customerId + "/payments"))
                    .POST(HttpRequest.BodyPublishers.ofString(dataToJson(input)))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Payment payment = (Payment) jsonToData(response.body(), Payment.class);
            executePayment(customerId, payment.getId().toString());
        } catch (Exception e) {
            System.out.println(response.body());
            e.printStackTrace();
        }
    }

    private void executePayment(String customerId, String paymentId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/" + customerId + "/payments/" + paymentId))
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private BigDecimal getAccountBalance(String customerId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL + "/customers/" + customerId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Customer customer = (Customer) jsonToData(response.body(), Customer.class);
        return customer.getAccounts().get(0).getTotalBalance();
    }
}
