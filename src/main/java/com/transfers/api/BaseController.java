package com.transfers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import spark.CustomErrorPages;
import spark.Service;

import java.io.IOException;
import java.io.StringWriter;

import static spark.Spark.halt;
import static spark.Spark.internalServerError;
import static spark.Spark.notFound;

public abstract class BaseController<T> {
    @Inject
    private ObjectMapper mapper;

    public String dataToJson(Object data) {
        try {
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, data);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException in dataToJson " + data, e);
        }
    }

    public T jsonToData(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("IOException in jsonToData " + json, e);
        }
    }

    abstract void configure(Service spark);
}
