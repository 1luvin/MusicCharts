package com.vance.lib;

import com.vance.lib.integration.LastFmIntegration;
import com.vance.lib.integration.MusicbrainzIntegration;
import com.vance.lib.integration.SpotifyIntegration;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.secrets.SecretProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ChartDataProvider {
    private final Logger log = LoggerFactory.getLogger(ChartDataProvider.class);
    private final RequestService requestService = RequestService.getInstance();
    private final SecretProvider secretProvider = SecretProvider.getInstance(requestService);
    private final MusicbrainzIntegration musicbrainz = new MusicbrainzIntegration(requestService);
    private final LastFmIntegration lastFM = new LastFmIntegration(requestService, secretProvider);
    private final SpotifyIntegration spotify = new SpotifyIntegration(requestService, secretProvider);

    public Map<String, Long> numberOfArtistsOfGenres(@NotNull String genre) {
        log.info("Getting number of artists of genres, genres: {}", genre);
        return musicbrainz.getNumberOfArtistsOfGenres(genre);
    }

    public Map<String, Long> numberOfReleasesOfGenres(@NotNull String genre) {
        log.info("Getting number of releases of genres, genres: {}", genre);
        return musicbrainz.getNumberOfReleasesOfGenres(genre);
    }

    public Map<String, Long> numberOfReleasesOfGenreInYears(int years, @NotNull String genre) {
        log.info("Getting releases of genre ({}) released in years ({})", genre, musicbrainz.getYearsFormatted(years));
        return musicbrainz.getNumberOfReleasesOfGenreInYears(years, genre);
    }

    public Map<String, Long> popularityOfGenres() {
        log.info("Getting popularity of genres");
        return lastFM.getPopularityOfGenres();
    }

    public Map<String, Integer> popularityOfAlbums(@NotNull String artist) {
        log.info("Getting popularity of albums of artist {}", artist);
        return spotify.getPopularityOfAlbums(artist);
    }

    public Map<String, Integer> popularityOfTracksOfArtist(@NotNull String artist) {
        log.info("Getting popularity of tracks of artist {}", artist);
        return spotify.getPopularityOfTracksOfArtist(artist);
    }

    public Map<String, Long> durationOfTracksInAlbum(@NotNull String album) {
        log.info("Getting duration of tracks of album {}", album);
        return spotify.getDurationOfTracksInAlbum(album);
    }

    public Map<String, Integer> popularityOfTracksInAlbum(@NotNull String album) {
        log.info("Getting popularity of tracks from album {}", album);
        return spotify.getPopularityOfTracksInAlbum(album);
    }

    public Map<Integer, List<String>> activityOfArtist(@NotNull String artist) {
        log.info("Getting activity of artist {}", artist);
        return spotify.getActivityOfArtist(artist);
    }

    public Map<String, Long> popularArtistsOfGenre(@NotNull String genre) {
        log.info("Getting popular artists of genre {}", genre);
        return spotify.getPopularArtistsOfGenre(genre);
    }

    public static void main(String[] args) {
        final RequestService requestServiceInstance = RequestService.getInstance();
        final SecretProvider secretProviderInstance = SecretProvider.getInstance(requestServiceInstance);
        SpotifyIntegration integration = new SpotifyIntegration(requestServiceInstance, secretProviderInstance);
        MusicbrainzIntegration musicbrainzIntegration = new MusicbrainzIntegration(requestServiceInstance);
        LastFmIntegration lastFmIntegration = new LastFmIntegration(requestServiceInstance, secretProviderInstance);

        musicbrainzIntegration.getNumberOfArtistsOfGenres("Rock");
        musicbrainzIntegration.getNumberOfReleasesOfGenres("Metal");
        musicbrainzIntegration.getNumberOfReleasesOfGenreInYears(MusicbrainzIntegration.YEARS_90_TO_99, "Rock");
        musicbrainzIntegration.getNumberOfReleasesOfGenreInYears(MusicbrainzIntegration.YEARS_00_TO_09, "Rock");
        musicbrainzIntegration.getNumberOfReleasesOfGenreInYears(MusicbrainzIntegration.YEARS_10_TO_19, "Rock");

        lastFmIntegration.getPopularityOfGenres();

        integration.getPopularArtistsOfGenre("Rock");
        integration.getPopularityOfTracksOfArtist("Deftones");
        integration.getPopularityOfAlbums("Queen");
        integration.getActivityOfArtist("Tool");
        integration.getPopularityOfTracksInAlbum("White Pony");
        integration.getDurationOfTracksInAlbum("Lateralus");

        System.out.println("Total requests: " + requestServiceInstance.getNumberOfRequests());
    }
}