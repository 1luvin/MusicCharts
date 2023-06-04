package com.vance.lib.service.web.http;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

import static java.lang.String.format;
import static org.apache.hc.client5.http.impl.classic.HttpClients.createDefault;

public class RequestService {
    private static final Logger log = LoggerFactory.getLogger(RequestService.class);
    private static RequestService instance = null;

    public static RequestService getInstance() {
        if (instance == null) {
            instance = new RequestService();
        }
        return instance;
    }

    private RequestService() {
        log.info("RequestService instance is created");
    }

    public String sendRequest(ClassicHttpRequest request) {
        final CloseableHttpClient httpClient = createDefault();
        try (httpClient) {
            log.debug("Sending {} request to {}", request.getMethod(), request.getUri().toString());
            return httpClient.execute(request, new CustomResponseHandler());
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
        }
        throw new IllegalStateException(format("Error with proceeding request to %s", request.getRequestUri()));
    }
}
