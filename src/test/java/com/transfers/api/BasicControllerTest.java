package com.transfers.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public class BasicControllerTest {
    private static ObjectMapper mapper = new ObjectMapper();

    public String dataToJson(Object data) throws IOException {
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, data);
            return sw.toString();
    }

    public Object jsonToData(String json, Class clazz) throws IOException{
            return mapper.readValue(json, clazz);
    }
}
