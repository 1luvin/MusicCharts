package com.vance.lib.service.web.url.configuration;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UrlConfiguration {

    protected final StringBuilder resultUrl = new StringBuilder();
    protected final Map<String, String> queryParameters = new HashMap<>();

    protected void addQueryParam(String key, String value) {
        queryParameters.put(key, value);
    }

    protected String encodeQuery(@NotNull String query) {
        try {
            return URLEncoder.encode(query, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Cannot encode query: " + query);
        }
    }

    public String build() {
        return resultUrl.toString();
    }
}
