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
    private final RequestService requestService = RequestService.getInstance();
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final SecretProvider secretProvider = SecretProvider.getInstance(requestService);

    public Map<String, Long> getPopularityOfGenres() {
        try {
            final String popularityOfGenresResponse = sendRequestToLastFm(urlBuilder.lastfm().getTopGenres().build());
            return parser.parsePopularityOfGenres(popularityOfGenresResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occurred", e);
        }
    }

    private String sendRequestToLastFm(String url) {
        final String finalUrl = String.format(url + "&api_key=%s&format=json", secretProvider.getLastFMToken());
        try {
            return requestService.sendRequest(requestBuilder.get().url(finalUrl).build());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Bad url given: %s", url));
        }
    }
}
