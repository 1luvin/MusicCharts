package com.vance.lib.service.web.secrets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.url.UrlBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import static com.vance.lib.util.FileUtil.readFile;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

public class SecretProvider {
    private static final Logger log = getLogger(SecretProvider.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Calendar calendar = new GregorianCalendar();
    private final RequestService requestService;
    private Integer lastUpdatedHour = -1;
    private Integer lastUpdatedDate = -1;
    private String spotifyClientSecret;
    private String spotifyClientID;
    private String spotifyToken;
    private String lastFMToken;

    private static SecretProvider secretProviderInstance = null;

    private SecretProvider(RequestService requestService) {
        this.requestService = requestService;
        setSecrets();
        log.info("SecretProvider Instance is created");
    }

    public static SecretProvider getInstance(RequestService requestService) {
        if (secretProviderInstance == null)
            secretProviderInstance = new SecretProvider(requestService);
        return secretProviderInstance;
    }

    private void setSecrets() {
        try {
            final String secrets = readFile("secrets.json");
            final JsonNode node = objectMapper.readTree(secrets);
            final JsonNode spotifyNode = node.get("spotify_credentials");
            lastFMToken = node.get("lastFM_token").asText();
            spotifyClientID = spotifyNode.get("client_id").asText();
            spotifyClientSecret = spotifyNode.get("client_secret").asText();
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
        }
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
            final String response = requestService.sendRequest(createSpotifyTokenRequest()
                    .orElseThrow(() -> new RuntimeException("Can't create spotify token request")));
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

        log.debug("Setting new HOUR of spotify token update, old value - {}, new value - {}", lastUpdatedHour, newHour);
        log.debug("Setting new DATE of spotify token update, old value - {}, new value - {}", lastUpdatedDate, newDate);

        lastUpdatedHour = newHour;
        lastUpdatedDate = newDate;
    }

    private Optional<ClassicHttpRequest> createSpotifyTokenRequest() {
        try {
            return Optional.of(new HttpRequestBuilder()
                    .post()
                    .url(new UrlBuilder().spotifyToken())
                    .body(format("grant_type=client_credentials&client_id=%s&client_secret=%s", spotifyClientID, spotifyClientSecret))
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build());
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }
}
