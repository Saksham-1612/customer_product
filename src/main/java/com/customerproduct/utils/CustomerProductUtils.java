package com.customerproduct.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CustomerProductUtils {
    private static final Logger log = LoggerFactory.getLogger(CustomerProductUtils.class);

    public static boolean isNotNullAndEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static Map<String, Object> setResponse(String message, Object object) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);

        if (object != null) {
            response.put("data", object);
        }
        return response;
    }
}
