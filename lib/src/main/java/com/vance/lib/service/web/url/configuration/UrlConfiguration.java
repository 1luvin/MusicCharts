package com.vance.lib.service.web.url.configuration;

import java.util.HashMap;
import java.util.Map;

public class UrlConfiguration {

    protected final StringBuilder resultUrl = new StringBuilder();
    protected final Map<String, String> queryParameters = new HashMap<>();

    protected void addQueryParam(String key, String value) {
        queryParameters.put(key, value);
    }

    public String build() {
        return resultUrl.toString();
    }
}
