package com.vance.lib.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.parser.LastFmParser;
import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.secrets.SecretProvider;
import com.vance.lib.service.web.url.UrlBuilder;

import java.net.URISyntaxException;
import java.util.Map;

public class LastFmIntegration {

    private final UrlBuilder urlBuilder = new UrlBuilder();
    private final LastFmParser parser = new LastFmParser();
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final RequestService requestService = RequestService.getInstance();
    private final SecretProvider secretProvider = SecretProvider.getInstance(requestService);

    public Map<String, Long> getPopularityOfGenres() {
        final String url = String.format(urlBuilder.lastfm().getTopGenres().build()
                + "&api_key=%s&format=json", secretProvider.getLastFMToken());

        try {
            return parser.parsePopularityOfGenres(requestService.sendRequest(requestBuilder.get().url(url).build()));
        } catch (JsonProcessingException e) {
            throw new LastFmIntegrationException("Error occurred", e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Bad url created: %s", url));
        }
    }

    static class LastFmIntegrationException extends RuntimeException {
        public LastFmIntegrationException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
