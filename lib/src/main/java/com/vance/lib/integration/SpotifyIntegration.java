package com.vance.lib.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.parser.SpotifyParser;
import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.secrets.SecretProvider;
import com.vance.lib.service.web.url.UrlBuilder;
import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class SpotifyIntegration {
    private final Logger log = LoggerFactory.getLogger(SpotifyIntegration.class);
    private final RequestService requestService;
    private final SecretProvider secretProvider;
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final SpotifyParser parser = new SpotifyParser();
    private final UrlBuilder urlBuilder = new UrlBuilder();
    private final boolean WITH_LIMIT = true;
    private final boolean WITHOUT_LIMIT = false;

    public SpotifyIntegration(RequestService requestService, SecretProvider secretProvider) {
        this.requestService = requestService;
        this.secretProvider = secretProvider;
    }

    public Map<String, Integer> getPopularityOfAlbums(@NotNull String artistName) {
        try {
            final String searchResponse = searchItem("artist:" + artistName, SpotifySearchTypes.ALBUM, WITHOUT_LIMIT);
            final String albumIds = parser.parseIdOfItems(searchResponse, SpotifySearchTypes.ALBUM);

            final String albums = sendRequestToSpotify(urlBuilder.spotify().album("?ids=" + albumIds).build());
            final Map<String, Integer> result = parser.parsePopularityOfAlbums(albums);

            log.debug("Getting popularity of albums: {}", result.toString());

            return result;
        } catch (JsonProcessingException e) {
            throw new SpotifyIntegrationException("Error occurred: " + e.getMessage(), e);
        }
    }

    public Map<String, Integer> getPopularityOfTracksOfArtist(@NotNull String artistName) {
        try {
            final String searchResponse = searchItem(artistName, SpotifySearchTypes.ARTIST, WITH_LIMIT);
            final String artistId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ARTIST, artistName);

            final String tracks = sendRequestToSpotify(urlBuilder.spotify().topTracksOfArtist(artistId).build());
            final Map<String, Integer> result = parser.parsePopularityOfTracksOfArtist(tracks);

            log.debug("Result of getting popularity of tracks of artist: {}", result.toString());

            return result;
        } catch (JsonProcessingException e) {
            throw new SpotifyIntegrationException("Error occurred: " + e.getMessage(), e);
        }
    }

    public Map<String, Long> getDurationOfTracksInAlbum(@NotNull String albumName) {
        try {
            final String searchResponse = searchItem(albumName, SpotifySearchTypes.ALBUM, WITHOUT_LIMIT);
            final String albumId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ALBUM, albumName);

            final String tracksOfAlbum = sendRequestToSpotify(urlBuilder.spotify().albumTracks(albumId).build());
            final Map<String, Long> result = parser.parseDurationOfTracksFromAlbum(tracksOfAlbum);

            log.debug("Result of getting duration of tracks in album: {}", result.toString());

            return result;
        } catch (JsonProcessingException e) {
            throw new SpotifyIntegrationException("Error occurred: " + e.getMessage(), e);
        }
    }

    public Map<String, Integer> getPopularityOfTracksInAlbum(@NotNull String albumName) {
        try {
            final String searchResponse = searchItem(albumName, SpotifySearchTypes.ALBUM, WITHOUT_LIMIT);
            final String albumId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ALBUM, albumName);

            final String tracksOfAlbum = sendRequestToSpotify(urlBuilder.spotify().albumTracks(albumId).build());
            final String parsedIdOfTracks = parser.parseIdOfTracks(tracksOfAlbum);

            final String tracks = sendRequestToSpotify(urlBuilder.spotify().track("?ids=" + parsedIdOfTracks).build());
            final Map<String, Integer> result = parser.parsePopularityOfTracksFromAlbum(tracks);

            log.debug("Result of getting popularity of tracks in album: {}", result.toString());

            return result;
        } catch (JsonProcessingException e) {
            throw new SpotifyIntegrationException("Error occurred: " + e.getMessage(), e);
        }
    }

    public Map<Integer, List<String>> getActivityOfArtist(@NotNull String artistName) {
        try {
            final String searchResponse = searchItem(artistName, SpotifySearchTypes.ARTIST, WITHOUT_LIMIT);
            final String artistId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ARTIST, artistName);

            final String albums = sendRequestToSpotify(urlBuilder.spotify().albumsOfArtist(artistId, false).build());
            final Map<Integer, List<String>> result = parser.parseActivityOfArtist(albums);

            log.debug("Result of getting activity of artist: {}", result.toString());

            return result;
        } catch (JsonProcessingException e) {
            throw new SpotifyIntegrationException("Error occurred: " + e.getMessage(), e);
        }
    }

    public Map<String, Long> getPopularArtistsOfGenre(@NotNull String genreName) {
        try {
            final String searchResponse = searchItem(String.format("genre:%s", genreName), SpotifySearchTypes.ARTIST, WITH_LIMIT);
            final Map<String, Long> result = parser.parsePopularityOfArtistsOfGenre(searchResponse);

            log.debug("Popularity of {} artists: {}", genreName, result.toString());

            return result;
        } catch (JsonProcessingException e) {
            throw new SpotifyIntegrationException("Error occurred: " + e.getMessage(), e);
        }
    }

    private String searchItem(@NotNull String itemName, @NotNull SpotifySearchTypes itemType, boolean limit) {
        return sendRequestToSpotify(urlBuilder.spotify().search(itemName, itemType, limit).build());
    }

    private String sendRequestToSpotify(String url) {
        try {
            return requestService.sendRequest(requestBuilder.get()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + secretProvider.getSpotifyToken())
                    .build());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Bad url created: %s", url));
        }
    }

    static class SpotifyIntegrationException extends RuntimeException {
        public SpotifyIntegrationException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
