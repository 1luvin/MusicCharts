package com.vance.lib.service.web.http;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class RequestService {
    private static final Logger log = LoggerFactory.getLogger(RequestService.class);

    private static RequestService instance = null;

    public static RequestService getInstance() {
        if (instance == null) {
            instance = new RequestService();
        }
        log.info("RequestService instance is created");
        return instance;
    }

    private RequestService() {
    }

    public String sendRequest(ClassicHttpRequest request) {
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        try (httpClient) {
            log.debug("Sending {} request to {}", request.getMethod(), request.getUri().toString());
            return httpClient.execute(request, new CustomResponseHandler());
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
        }
        throw new IllegalStateException(String.format("Error with proceeding request to %s", request.getRequestUri()));
    }
}
