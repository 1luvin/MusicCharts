package com.vance.lib.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UrlBuilder {
    private final Logger log = LoggerFactory.getLogger(UrlBuilder.class);
    private final String DISCOGS_BASE_URL = "https://api.discogs.com";
    private final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1";
    private final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";
    private final String MUSICBRAINZ_BASE_URL = "https://musicbrainz.org/ws/2";
    private final String LASTFM_BASE_URL = " http://ws.audioscrobbler.com/2.0";
    private final String REINITIALIZATION_MESSAGE = "Reinitializing base url to %s";

    private final SecretProvider secretProvider = SecretProvider.getInstance();
    private final StringBuilder url = new StringBuilder();
    private final Map<String, String> queryParameters = new HashMap<>();

    public String build() {
        addQueryParametersToUrl();
        return url.toString();
    }

    public UrlBuilder discogs() {
        setUrlBase(DISCOGS_BASE_URL);
        return this;
    }

    public UrlBuilder spotify() {
        setUrlBase(SPOTIFY_BASE_URL);
        return this;
    }

    public UrlBuilder spotifyToken() {
        setUrlBase(SPOTIFY_TOKEN_URL);
        return this;
    }

    public UrlBuilder lastfm() {
        setUrlBase(LASTFM_BASE_URL);
        return this;
    }

    public UrlBuilder musicbrainz() {
        setUrlBase(MUSICBRAINZ_BASE_URL);
        return this;
    }

    private void setUrlBase(String urlBase) {
        if (url.length() != 0)
            log.debug(String.format(REINITIALIZATION_MESSAGE, urlBase));
        url.delete(0, url.length() - 1);
        url.append(urlBase);
    }

    private void setUrlBase(String urlBase, String apiKey) {
        setUrlBase(urlBase);
        url.append("token=").append(apiKey);
    }

    private void addQueryParametersToUrl() {
        if (url.length() == 0)
            throw new IllegalStateException("Adding query parameters to empty url");
        url.append("?");
        queryParameters.entrySet()
                .forEach(entry -> url.append(entry.getKey())
                        .append("=")
                        .append(entry)
                        .append("&"));
        url.deleteCharAt(url.length() - 1);
    }
}
