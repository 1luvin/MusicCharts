package com.vance.lib.service.web.secrets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.url.UrlBuilder;
import com.vance.lib.util.FileUtil;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SecretProvider {
    private String discogsToken;
    private String lastFMToken;
    private String spotifyClientID;
    private String spotifyClientSecret;
    private String spotifyToken;
    private Integer lastUpdatedHour = -1;
    private Integer lastUpdatedDate = -1;
    private final Logger log = LoggerFactory.getLogger(SecretProvider.class);
    private final RequestService requestService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Calendar calendar = new GregorianCalendar();
    private static SecretProvider secretProviderInstance = null;

    private SecretProvider(RequestService requestService) {
        this.requestService = requestService;
        setSecrets();
    }

    public static SecretProvider getInstance(RequestService requestService) {
        if (secretProviderInstance == null) {
            secretProviderInstance = new SecretProvider(requestService);
        }
        return secretProviderInstance;
    }

    private void setSecrets() {
        try {
            final String secrets = FileUtil.readFile("secrets.json");
            JsonNode node = objectMapper.readTree(secrets);
            JsonNode spotifyNode = node.get("spotify_credentials");
            discogsToken = node.get("discogs_token").asText();
            lastFMToken = node.get("lastFM_token").asText();
            spotifyClientID = spotifyNode.get("client_id").asText();
            spotifyClientSecret = spotifyNode.get("client_secret").asText();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public String getDiscogsToken() {
        return discogsToken;
    }

    public String getLastFMToken() {
        return lastFMToken;
    }

    public String getSpotifyToken() {
        calendar.setTime(new Date());
        if (calendar.get(Calendar.HOUR) > lastUpdatedHour || calendar.get(Calendar.DATE) > lastUpdatedDate) {
            updateSpotifyToken();
        }
        return spotifyToken;
    }

    private void updateSpotifyToken() {
        try {
            final String response = requestService.sendRequest(createSpotifyTokenRequest());
            spotifyToken = objectMapper.readTree(response).get("access_token").asText();
            resetLastUpdatedDateAndHour();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private void resetLastUpdatedDateAndHour() {
        calendar.setTime(new Date());
        final int newHour = calendar.get(Calendar.HOUR);
        final int newDate = calendar.get(Calendar.DATE);

        log.debug(String.format("Setting new hour of spotify token update, old value - %s, new value - %s",
                lastUpdatedHour, newHour));
        log.debug(String.format("Setting new date of spotify token update, old value - %s, new value - %s",
                lastUpdatedDate, newDate));

        lastUpdatedHour = newHour;
        lastUpdatedDate = newDate;
    }

    private ClassicHttpRequest createSpotifyTokenRequest() {
        try {
            return new HttpRequestBuilder()
                    .post()
                    .url(new UrlBuilder().spotifyToken())
                    .body(String.format("grant_type=client_credentials&client_id=%s&client_secret=%s",
                            spotifyClientID, spotifyClientSecret))
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        throw new IllegalStateException("Cannot create request for spotify token");
    }
}
