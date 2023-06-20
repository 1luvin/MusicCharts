package com.vance.lib.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.parser.SpotifyParser;
import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.secrets.SecretProvider;
import com.vance.lib.service.web.url.UrlBuilder;
import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class SpotifyIntegration {
    private final Logger log = LoggerFactory.getLogger(SpotifyIntegration.class);
    private final RequestService requestService;
    private final SecretProvider secretProvider;
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final SpotifyParser parser = new SpotifyParser();
    private final UrlBuilder urlBuilder = new UrlBuilder();
    private final boolean WITH_LIMIT = true;
    private final boolean WITHOUT_LIMIT = false;
    private String cachedAlbumId = null;
    private String cachedArtistId = null;
    private String cachedAlbumName = null;
    private String cachedArtistName = null;
    private final int MAX_TRIES = 10;

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

    public Map<String, Integer> getPopularityOfTracksOfArtist(@NotNull String artist) {
        try {
            final String tracks = sendRequestToSpotify(urlBuilder.spotify().topTracksOfArtist(getArtistID(artist)).build());
            final Map<String, Integer> result = parser.parsePopularityOfTracksOfArtist(tracks);

            log.debug("Result of getting popularity of tracks of artist: {}", result.toString());

            return result;
        } catch (JsonProcessingException e) {
            throw new SpotifyIntegrationException("Error occurred: " + e.getMessage(), e);
        }
    }

    public Map<String, Long> getDurationOfTracksInAlbum(@NotNull String album) {
        try {
            final String tracks = sendRequestToSpotify(urlBuilder.spotify().albumTracks(getAlbumID(album)).build());
            final Map<String, Long> result = parser.parseDurationOfTracksFromAlbum(tracks);

            log.debug("Result of getting duration of tracks in album: {}", result.toString());

            return result;
        } catch (JsonProcessingException e) {
            throw new SpotifyIntegrationException("Error occurred: " + e.getMessage(), e);
        }
    }

    public Map<String, Integer> getPopularityOfTracksInAlbum(@NotNull String album) {
        try {
            final String tracksOfAlbum = sendRequestToSpotify(urlBuilder.spotify().albumTracks(getAlbumID(album)).build());
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
            final String albums = sendRequestToSpotify(urlBuilder.spotify().albumsOfArtist(getArtistID(artistName), false).build());
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
        final AtomicReference<String> result = new AtomicReference<>();
        final String searchUrl = urlBuilder.spotify().search(itemName, itemType, limit).build();
        final long numberOfTries = IntStream.range(1, MAX_TRIES + 1)
                .boxed()
                .takeWhile(n -> tryToSearch(result, searchUrl, n))
                .count();

        log.debug("Number of tries: {}", numberOfTries);
        if (numberOfTries == MAX_TRIES) {
            log.error("Problem with searching {}, item for search: {}", itemType.name().toLowerCase(), itemName);
            throw new IllegalStateException("Exceeded maximum number of retries");
        }
        return result.get();
    }

    private boolean tryToSearch(AtomicReference<String> reference, String url, int numberOfTry) {
        try {
            reference.set(sendRequestToSpotify(url));
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            log.warn("Trying to resend the request for positive response, number of try: {}", numberOfTry);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        return reference.get() == null;
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

    private String getAlbumID(String albumName) throws JsonProcessingException {
        if (!StringUtils.equals(albumName, cachedAlbumName)) {
            final String searchResponse = searchItem(albumName, SpotifySearchTypes.ALBUM, WITHOUT_LIMIT);
            cachedAlbumName = albumName;
            cachedAlbumId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ALBUM, albumName);
        }

        return cachedAlbumId;
    }

    private String getArtistID(String artistName) throws JsonProcessingException {
        if (!StringUtils.equals(artistName, cachedArtistName)) {
            final String searchResponse = searchItem(artistName, SpotifySearchTypes.ARTIST, WITHOUT_LIMIT);
            cachedArtistName = artistName;
            cachedArtistId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ARTIST, artistName);
        }

        return cachedArtistId;
    }

    static class SpotifyIntegrationException extends RuntimeException {
        public SpotifyIntegrationException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
