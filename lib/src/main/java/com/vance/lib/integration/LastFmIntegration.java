package com.vance.lib.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.parser.LastFmParser;
import com.vance.lib.service.parser.ParsingException;
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
    private final RequestService requestService;
    private final SecretProvider secretProvider;

    public LastFmIntegration(RequestService requestService, SecretProvider secretProvider) {
        this.requestService = requestService;
        this.secretProvider = secretProvider;
    }

    public Map<String, Long> getPopularityOfGenres() throws IntegrationException {
        final String baseUrl = urlBuilder.lastfm().getTopGenres().build() + "&api_key=%s&format=json";
        final String url = String.format(baseUrl, secretProvider.getLastFMToken());

        try {
            return parser.parsePopularityOfGenres(requestService.sendRequest(requestBuilder.get().url(url).build()));
        } catch (JsonProcessingException | ParsingException e) {
            throw new LastFmIntegrationException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new LastFmIntegrationException(String.format("Bad url created: %s", url), e);
        }
    }

    static class LastFmIntegrationException extends IntegrationException {
        public LastFmIntegrationException(String message, Exception throwable) {
            super(message, throwable);
        }
    }
}
