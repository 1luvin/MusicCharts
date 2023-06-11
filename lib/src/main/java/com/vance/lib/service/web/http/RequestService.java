package com.vance.lib.service.web.http;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static java.lang.String.format;
import static org.apache.hc.client5.http.impl.classic.HttpClients.createDefault;

public class RequestService {
    private static final Logger log = LoggerFactory.getLogger(RequestService.class);
    private static RequestService instance = null;
    private final CustomResponseHandler responseHandler = new CustomResponseHandler();

    public static RequestService getInstance() {
        if (instance == null) instance = new RequestService();
        return instance;
    }

    private RequestService() {
        log.info("RequestService instance is created");
    }

    public String sendRequest(ClassicHttpRequest request) {
        final CloseableHttpClient httpClient = createDefault();
        try (httpClient) {
            log.debug("Sending {} request to {}", request.getMethod(), request.getUri().toString());
            return httpClient.execute(request, responseHandler);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
        }
        throw new IllegalStateException(format("Error with proceeding request to %s", request.getRequestUri()));
    }

    private static class CustomResponseHandler implements HttpClientResponseHandler<String> {
        private final Logger log = LoggerFactory.getLogger(CustomResponseHandler.class);

        @Override
        public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
            final int responseCode = response.getCode();
            log.debug("Got response with code - {}", responseCode);

            final String responseBody = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());

            if (responseCode != HttpStatus.SC_OK) {
                log.error("Response code - {}, Response body:\n{}", responseCode, responseBody);
                throw new HttpException("Bad response code: {}", responseCode);
            } else {
                log.debug("Response body:\n{}", responseBody);
            }

            return responseBody;
        }
    }
}
