package com.transfers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import spark.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

public abstract class BaseController<T> {
    @Inject
    private ObjectMapper mapper;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    public String dataToJson(Object data) {
        try {
            mapper.setDateFormat(dateFormat);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
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
