package com.vance.lib.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.parser.SpotifyParser;
import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.secrets.SecretProvider;
import com.vance.lib.service.web.url.UrlBuilder;
import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.List;

public class SpotifyIntegration {
    private final Logger log = LoggerFactory.getLogger(SpotifyIntegration.class);
    private final RequestService requestService = RequestService.getInstance();
    private final SecretProvider secretProvider = SecretProvider.getInstance(requestService);
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final SpotifyParser parser = new SpotifyParser();
    private final UrlBuilder urlBuilder = new UrlBuilder();

    public String getInfoAboutArtist(@NotNull String artistName) {
        return searchItem(artistName, SpotifySearchTypes.ARTIST);
    }

    public List<Pair<String, Integer>> getPopularityOfAlbums(@NotNull String artistName) {
        try {
            final String searchResponse = searchItem("artist:" + artistName, SpotifySearchTypes.ALBUM);
            final String albumIds = parser.parseIdOfItems(searchResponse, SpotifySearchTypes.ALBUM);
            final String albumsResponse = requestService.sendRequest(requestBuilder.get()
                    .url(urlBuilder.spotify()
                            .album("?ids=" + albumIds)
                            .build())
                    .addHeader("Authorization", "Bearer " + secretProvider.getSpotifyToken())
                    .build());
            return parser.parsePopularityOfAlbums(albumsResponse);
        } catch (JsonProcessingException | URISyntaxException e) {
            throw new RuntimeException("Error occurred", e);
        }
    }

    public List<Pair<String, Long>> getDurationOfTracksInAlbum(@NotNull String albumName) {
        try {
            final String searchResponse = searchItem(albumName, SpotifySearchTypes.ALBUM);
            final String albumId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ALBUM);
            final String tracksOfAlbumResponse = requestService.sendRequest(requestBuilder.get()
                    .url(urlBuilder.spotify()
                            .albumTracks(albumId)
                            .build())
                    .addHeader("Authorization", "Bearer " + secretProvider.getSpotifyToken())
                    .build());
            return parser.parseDurationOfTracksFromAlbum(tracksOfAlbumResponse);
        } catch (JsonProcessingException | URISyntaxException e) {
            throw new RuntimeException("Error occurred", e);
        }
    }

    private String searchItem(@NotNull String itemName, @NotNull SpotifySearchTypes itemType) {
        try {
            return requestService.sendRequest(requestBuilder.get()
                    .url(urlBuilder.spotify()
                            .search(itemName, itemType)
                            .build())
                    .addHeader("Authorization", "Bearer " + secretProvider.getSpotifyToken())
                    .build());
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        throw new IllegalStateException(String.format("Failed to search item: %s", itemName));
    }
}
