package com.vance.lib.service.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vance.lib.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SecretProvider {
    private String discogsToken;
    private String lastFMToken;
    private String spotifyClientID;
    private String spotifyClientSecret;
    private static Logger log = LoggerFactory.getLogger(SecretProvider.class);
    private static SecretProvider secretProviderInstance = null;

    private SecretProvider() {
        setSecrets();
    }

    public static SecretProvider getInstance() {
        if (secretProviderInstance == null) {
            secretProviderInstance = new SecretProvider();
        }
        return secretProviderInstance;
    }

    private void setSecrets() {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final String secrets = FileUtil.readFile("secrets.json");
            JsonNode node = objectMapper.readTree(secrets);
            JsonNode spotifyNode = node.get("spotify_credentials");
            setDiscogsToken(node.get("discogs_token").asText());
            setLastFMToken(node.get("lastFM_token").asText());
            setSpotifyClientID(spotifyNode.get("clientID").asText());
            setSpotifyClientSecret(spotifyNode.get("clientSecret").asText());
        } catch (IOException e) {
            log.error("Failed to read secrets file");
        }
    }

    public String getDiscogsToken() {
        return discogsToken;
    }

    public void setDiscogsToken(String discogsToken) {
        this.discogsToken = discogsToken;
    }

    public String getLastFMToken() {
        return lastFMToken;
    }

    public void setLastFMToken(String lastFMToken) {
        this.lastFMToken = lastFMToken;
    }

    public String getSpotifyClientID() {
        return spotifyClientID;
    }

    public void setSpotifyClientID(String spotifyClientID) {
        this.spotifyClientID = spotifyClientID;
    }

    public String getSpotifyClientSecret() {
        return spotifyClientSecret;
    }

    public void setSpotifyClientSecret(String spotifyClientSecret) {
        this.spotifyClientSecret = spotifyClientSecret;
    }
}
