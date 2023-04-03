package com.vance.lib.service.web;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RequestService {
    private static final Logger log = LoggerFactory.getLogger(RequestService.class);

    public static String sendRequest(ClassicHttpRequest request) {
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        try (httpClient) {
            return httpClient.execute(request, new CustomResponseHandler());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        throw new IllegalStateException("Request sending failure");
    }
}
