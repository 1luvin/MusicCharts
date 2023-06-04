package com.vance.lib.service.web.http;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.logging.log4j.core.util.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpRequestBuilderTest {

    private final HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
    private final String TEST_URL = "https://test.url/";

    @Test
    void shouldCreateHttpGetRequest() throws URISyntaxException {
        // when
        final ClassicHttpRequest request = httpRequestBuilder.get().url(TEST_URL).build();

        // then
        assertEquals(TEST_URL, request.getUri().toString());
        assertEquals("GET", request.getMethod());
    }

    @Test
    void shouldCreateHttpPostRequest() throws URISyntaxException, IOException {
        // given
        final String testBody = "TEST_BODY";

        // when
        final ClassicHttpRequest request = httpRequestBuilder.post().url(TEST_URL).body(testBody).build();

        // then
        assertEquals(TEST_URL, request.getUri().toString());
        assertEquals("POST", request.getMethod());
        assertEquals(testBody, IOUtils.toString(new InputStreamReader(request.getEntity().getContent())));
    }

    @Test
    void shouldThrowWhenBuildingUninitializedRequest() {
        // when & then
        assertThrows(IllegalStateException.class, httpRequestBuilder::build);
    }

    @Test
    void shouldThrowWhenUrlIsNotSet() {
        // when & then
        assertThrows(IllegalStateException.class, () -> httpRequestBuilder.get().build());
        assertThrows(IllegalStateException.class, () -> httpRequestBuilder.post().build());
    }

    @Test
    void shouldThrowExceptionWhileAddingBodyToGetRequest() {
        // given
        final String testBody = "TEST_BODY";

        // when & then
        assertThrows(IllegalStateException.class, () -> httpRequestBuilder.get().body(testBody).build());
    }
}