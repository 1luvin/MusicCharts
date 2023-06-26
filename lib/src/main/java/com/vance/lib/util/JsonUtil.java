package com.vance.lib.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getText(JsonNode node, String name) {
        try {
            return node.get(name).asText();
        } catch (Exception e) {
            throw new RuntimeException("Parsing error", e);
        }
    }

    public static int getInt(JsonNode node, String name) {
        try {
            return node.get(name).asInt();
        } catch (Exception e) {
            throw new RuntimeException("Parsing error", e);
        }
    }

    public static long getLong(JsonNode node, String name) {
        try {
            return node.get(name).asLong();
        } catch (Exception e) {
            throw new RuntimeException("Parsing error", e);
        }
    }

    public static JsonNode readTree(String data) throws JsonProcessingException {
        try {
            return objectMapper.readTree(data);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new RuntimeException("Parsing error", e);
        }
    }
}
