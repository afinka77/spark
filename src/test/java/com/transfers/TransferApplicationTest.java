/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.transfers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.mockito.Spy;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransferApplicationTest {
    private static final String PORT = "8731";

    @Test
    public void main_run_restAPIIsUp() throws IOException, InterruptedException {
        TransferApplication.main(new String[]{"-port",PORT});
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8731/customers"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNotNull(response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void ObjectMapperProvider_get_objectMapper() {
        ObjectMapper objectMapper = new TransferApplication.ObjectMapperProvider().get();

        assertNotNull(objectMapper);
    }
}
