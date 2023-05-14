package com.vance.lib.service.web.http;

import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class CustomResponseHandler implements HttpClientResponseHandler<String> {
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
