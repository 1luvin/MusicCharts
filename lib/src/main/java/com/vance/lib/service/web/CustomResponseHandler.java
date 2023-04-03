package com.vance.lib.service.web;

import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.nio.charset.Charset;

public class CustomResponseHandler implements HttpClientResponseHandler<String> {

    @Override
    public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        final int responseCode = response.getCode();
        if (responseCode != HttpStatus.SC_OK)
            throw new HttpException(String.format("Bad response code: %s", responseCode));
        return IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
    }
}
