package com.example.aerospikedemo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
