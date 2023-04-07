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
import java.util.Map;

public class SpotifyIntegration {
    private final Logger log = LoggerFactory.getLogger(SpotifyIntegration.class);
    private final RequestService requestService = RequestService.getInstance();
    private final SecretProvider secretProvider = SecretProvider.getInstance(requestService);
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final SpotifyParser parser = new SpotifyParser();
    private final UrlBuilder urlBuilder = new UrlBuilder();


    public Map<String, Integer> getPopularityOfAlbums(@NotNull String artistName) {
        try {
            final String searchResponse = searchItem("artist:" + artistName, SpotifySearchTypes.ALBUM);
            final String albumIds = parser.parseIdOfItems(searchResponse, SpotifySearchTypes.ALBUM);

            final String albumsResponse = sendRequestToSpotify(urlBuilder.spotify().album("?ids=" + albumIds).build());
            return parser.parsePopularityOfAlbums(albumsResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occurred", e);
        }
    }

    public Map<String, Integer> getPopularityOfTracksOfArtist(@NotNull String artistName) {
        try {
            final String searchResponse = searchItem(artistName, SpotifySearchTypes.ARTIST);
            final String artistId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ARTIST);

            final String tracksResponse = sendRequestToSpotify(urlBuilder.spotify().topTracksOfArtist(artistId).build());
            return parser.parsePopularityOfTracks(tracksResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occurred", e);
        }
    }

    public Map<String, Long> getDurationOfTracksInAlbum(@NotNull String albumName) {
        try {
            final String searchResponse = searchItem(albumName, SpotifySearchTypes.ALBUM);
            final String albumId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ALBUM);

            final String tracksOfAlbumResponse = sendRequestToSpotify(urlBuilder.spotify().albumTracks(albumId).build());
            return parser.parseDurationOfTracksFromAlbum(tracksOfAlbumResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occurred", e);
        }
    }

    public Map<String, Integer> getPopularityOfTracksInAlbum(@NotNull String albumName) {
        try {
            final String searchResponse = searchItem(albumName, SpotifySearchTypes.ALBUM);
            final String albumId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ALBUM);

            final String tracksOfAlbumResponse = sendRequestToSpotify(urlBuilder.spotify().albumTracks(albumId).build());
            final String parsedIdOfTracks = parser.parseIdOfTracks(tracksOfAlbumResponse);

            final String tracksResponse = sendRequestToSpotify(urlBuilder.spotify().track("?ids=" + parsedIdOfTracks).build());
            return parser.parsePopularityOfTracksFromAlbum(tracksResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occurred", e);
        }
    }

    private String searchItem(@NotNull String itemName, @NotNull SpotifySearchTypes itemType) {
        return sendRequestToSpotify(urlBuilder.spotify()
                .search(itemName, itemType)
                .build());
    }

    private String sendRequestToSpotify(String url) {
        try {
            return requestService.sendRequest(requestBuilder.get()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + secretProvider.getSpotifyToken())
                    .build());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Bad url given: %s", url));
        }
    }
}
