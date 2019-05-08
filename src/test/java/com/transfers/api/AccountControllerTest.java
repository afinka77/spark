package com.transfers.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.transfers.api.dto.AccountDto;
import com.transfers.domain.Account;
import com.transfers.service.AccountService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spark.Service;

import javax.inject.Provider;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountControllerTest extends BasicControllerTest{
    private static final int PORT = 8765;
    private static final String URL = "http://localhost:" + PORT;
    private Service spark;
    private HttpClient client = HttpClient.newHttpClient();

    private AccountService accountService = mock(AccountService.class);

    @Before
    public void setUp(){
        spark = Service.ignite().port(PORT);
        getInjector().getInstance(AccountController.class).configure(spark);
        spark.awaitInitialization();
    }

    @After
    public void tearDown(){
        spark.stop();
        spark.awaitStop();
    }

    @Test
    public void accountController_endpoints_200() throws Exception{
        post_accountDto_insertedAccount();
        put_accountDto_updatedAccount();
        delete_accountId_200();
    }

    private void post_accountDto_insertedAccount() throws Exception {
        AccountDto input = AccountDto.builder()
                .name("LT999999999999999")
                .build();
        Account expected = Account.builder()
                .name("LT999999999999999")
                .build();
        when(accountService.insertAccount("1",input)).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL +"/customers/1/accounts"))
                .POST(HttpRequest.BodyPublishers.ofString(dataToJson(input)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200,response.statusCode());
        assertEquals(expected, jsonToData(response.body(),Account.class));
    }


    private void put_accountDto_updatedAccount() throws Exception {
        AccountDto input = AccountDto.builder()
                .name("LT999999999999999")
                .build();
        Account expected = Account.builder()
                .name("LT999999999999999")
                .build();
        when(accountService.updateAccount("1","2",input)).thenReturn(expected);
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL +"/customers/1/accounts/2"))
                .PUT(HttpRequest.BodyPublishers.ofString(dataToJson(input)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200,response.statusCode());
        assertEquals(expected, jsonToData(response.body(),Account.class));
    }


    private void delete_accountId_200() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL +"/customers/1/accounts/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200,response.statusCode());
    }

    private Injector getInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(AccountController.class);
                bind(AccountService.class).toProvider(new Provider<AccountService>() {
                    public AccountService get() {
                        return accountService;
                    }
                });
            }
        });
    }
}