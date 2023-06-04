package com.vance.lib.service.web.http;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class HttpRequestBuilder {

    private ClassicHttpRequest request = null;

    public ClassicHttpRequest build() throws URISyntaxException {
        ClassicHttpRequest result = Optional.ofNullable(request).orElseThrow(() -> {
            request = null;
            return new IllegalStateException("Building uninitialized request");
        });
        if ("/".equals(result.getUri().toString())) {
            request = null;
            throw new IllegalStateException("Request url is not set");
        }
        request = null;
        return result;
    }

    public HttpRequestBuilder get() {
        request = new HttpGet("");
        return this;
    }

    public HttpRequestBuilder post() {
        request = new HttpPost("");
        return this;
    }

    //todo: add methods if needed

    public HttpRequestBuilder url(String url) throws URISyntaxException {
        Optional.ofNullable(request).orElseThrow(() -> new IllegalStateException("Setting url on uninitialized request"));
        request.setUri(new URI(url));
        return this;
    }

    public HttpRequestBuilder addHeader(@NotNull String name, @NotNull String value) {
        Optional.ofNullable(request).orElseThrow(() -> new IllegalStateException("Adding header to uninitialized request"));
        request.addHeader(name, value);
        return this;
    }

    public HttpRequestBuilder body(@NotNull String body) {
        Optional.ofNullable(request).orElseThrow(() -> new IllegalStateException("Adding body to uninitialized request"));
        if (request instanceof HttpGet) {
            throw new IllegalStateException("Adding body to GET Http request is not allowed");
        }
        request.setEntity(HttpEntities.create(body));
        return this;
    }
}
