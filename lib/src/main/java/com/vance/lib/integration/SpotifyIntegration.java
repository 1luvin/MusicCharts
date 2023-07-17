package com.vance.lib.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.parser.ParsingException;
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

import static java.lang.String.format;

public class SpotifyIntegration {
    private final Logger log = LoggerFactory.getLogger(SpotifyIntegration.class);
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final SpotifyParser parser = new SpotifyParser();
    private final UrlBuilder urlBuilder = new UrlBuilder();

    private final RequestService requestService;
    private final SecretProvider secretProvider;

    private final boolean WITHOUT_LIMIT = false;
    private final boolean WITH_LIMIT = true;

    private String cachedArtistName = null;
    private String cachedAlbumName = null;
    private String cachedArtistId = null;
    private String cachedAlbumId = null;

    private final int MAX_TRIES = 10;
    private final int WAIT_TIME = 600;

    public SpotifyIntegration(RequestService requestService, SecretProvider secretProvider) {
        this.requestService = requestService;
        this.secretProvider = secretProvider;
    }

    public Map<String, Integer> getPopularityOfAlbums(@NotNull String artistName) throws IntegrationException {
        try {
            final String searchResponse = searchItem(format("artist:%s", artistName), SpotifySearchTypes.ALBUM, WITHOUT_LIMIT);
            final String albumIds = parser.parseIdOfItems(searchResponse, SpotifySearchTypes.ALBUM);

            final String albums = sendRequestToSpotify(urlBuilder.spotify().albums(albumIds).build());
            final Map<String, Integer> result = parser.parsePopularityOfAlbums(albums);

            log.debug("Getting popularity of albums: {}", result.toString());
            validateResult(result);
            return result;
        } catch (JsonProcessingException | ParsingException e) {
            throw new SpotifyIntegrationException(e.getMessage(), e);
        }
    }

    public Map<String, Integer> getPopularityOfTracksOfArtist(@NotNull String artist) throws IntegrationException {
        try {
            final String tracks = sendRequestToSpotify(urlBuilder.spotify().topTracksOfArtist(getArtistID(artist)).build());
            final Map<String, Integer> result = parser.parsePopularityOfTracksOfArtist(tracks);

            log.debug("Result of getting popularity of tracks of artist: {}", result.toString());
            validateResult(result);
            return result;
        } catch (JsonProcessingException | ParsingException e) {
            throw new SpotifyIntegrationException(e.getMessage(), e);
        }
    }

    public Map<String, Long> getDurationOfTracksInAlbum(@NotNull String album) throws IntegrationException {
        try {
            final String tracks = sendRequestToSpotify(urlBuilder.spotify().albumTracks(getAlbumID(album)).build());
            final Map<String, Long> result = parser.parseDurationOfTracksFromAlbum(tracks);

            log.debug("Result of getting duration of tracks in album: {}", result.toString());
            validateResult(result);
            return result;
        } catch (JsonProcessingException | ParsingException e) {
            throw new SpotifyIntegrationException(e.getMessage(), e);
        }
    }

    public Map<String, Integer> getPopularityOfTracksInAlbum(@NotNull String album) throws IntegrationException {
        try {
            final String tracksOfAlbum = sendRequestToSpotify(urlBuilder.spotify().albumTracks(getAlbumID(album)).build());
            final String parsedIdOfTracks = parser.parseIdOfTracks(tracksOfAlbum);

            final String tracks = sendRequestToSpotify(urlBuilder.spotify().track(format("?ids=%s", parsedIdOfTracks)).build());
            final Map<String, Integer> result = parser.parsePopularityOfTracksFromAlbum(tracks);

            log.debug("Result of getting popularity of tracks in album: {}", result.toString());
            validateResult(result);
            return result;
        } catch (JsonProcessingException | ParsingException e) {
            throw new SpotifyIntegrationException(e.getMessage(), e);
        }
    }

    public Map<Integer, List<String>> getActivityOfArtist(@NotNull String artistName) throws IntegrationException {
        try {
            final String albums = sendRequestToSpotify(urlBuilder.spotify().albumsOfArtist(getArtistID(artistName), false).build());
            final Map<Integer, List<String>> result = parser.parseActivityOfArtist(albums);

            log.debug("Result of getting activity of artist: {}", result.toString());
            validateResult(result);
            return result;
        } catch (JsonProcessingException | ParsingException e) {
            throw new SpotifyIntegrationException(e.getMessage(), e);
        }
    }

    public Map<String, Long> getPopularArtistsOfGenre(@NotNull String genreName) throws IntegrationException {
        try {
            final String searchResponse = searchItem(format("genre:%s", genreName), SpotifySearchTypes.ARTIST, WITH_LIMIT);
            final Map<String, Long> result = parser.parsePopularityOfArtistsOfGenre(searchResponse);

            log.debug("Popularity of {} artists: {}", genreName, result.toString());
            validateResult(result);
            return result;
        } catch (JsonProcessingException e) {
            throw new SpotifyIntegrationException(e.getMessage(), e);
        }
    }

    private String searchItem(@NotNull String itemName, @NotNull SpotifySearchTypes itemType, boolean limit) {
        final AtomicReference<String> result = new AtomicReference<>();
        final String searchUrl = urlBuilder.spotify().search(itemName, itemType, limit).build();
        int numberOfTries = 1;

        while (tryToSearch(result, searchUrl, numberOfTries) && numberOfTries != MAX_TRIES) numberOfTries++;

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
            warnAndWait(e.getMessage(), numberOfTry);
        }
        return reference.get() == null;
    }

    private void warnAndWait(String warnMessage, int numberOfTry) {
        log.warn(warnMessage);
        log.warn("Trying to resend the request for positive response, number of try: {}", numberOfTry);
        try {
            Thread.sleep(WAIT_TIME);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String sendRequestToSpotify(String url) {
        try {
            return requestService.sendRequest(requestBuilder.get()
                    .url(url)
                    .addHeader("Authorization", format("Bearer %s", secretProvider.getSpotifyToken()))
                    .build());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(format("Bad url created: %s", url));
        }
    }

    private String getAlbumID(String albumName) throws JsonProcessingException, ParsingException {
        if (!StringUtils.equals(albumName, cachedAlbumName)) {
            log.debug("Updating cached album from {} to {}", cachedAlbumName, albumName);

            final String searchResponse = searchItem(albumName, SpotifySearchTypes.ALBUM, WITHOUT_LIMIT);
            cachedAlbumName = albumName;
            cachedAlbumId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ALBUM, albumName);
        }

        return cachedAlbumId;
    }

    private String getArtistID(String artistName) throws JsonProcessingException, ParsingException {
        if (!StringUtils.equals(artistName, cachedArtistName)) {
            log.debug("Updating cached artist from {} to {}", cachedArtistName, artistName);

            final String searchResponse = searchItem(artistName, SpotifySearchTypes.ARTIST, WITHOUT_LIMIT);
            cachedArtistName = artistName;
            cachedArtistId = parser.parseIdOfItem(searchResponse, SpotifySearchTypes.ARTIST, artistName);
        }
        return cachedArtistId;
    }

    private <K, V> void validateResult(Map<K, V> result) throws IntegrationException {
        if (result.size() < 3) throw new IntegrationException("Bad data found, try something else");
    }

    static class SpotifyIntegrationException extends IntegrationException {
        public SpotifyIntegrationException(String message, Exception throwable) {
            super(message, throwable);
        }
    }
}
