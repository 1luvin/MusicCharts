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

    public Map<String, Long> genre_numberOfArtists(@NotNull String genre) {
        log.info("Getting number of artists of genres, genres: {}", genre);
        return musicbrainz.getNumberOfArtistsOfGenres(genre);
    }

    public Map<String, Long> genre_numberOfReleases(@NotNull String genre) {
        log.info("Getting number of releases of genres, genres: {}", genre);
        return musicbrainz.getNumberOfReleasesOfGenres(genre);
    }

    public Map<String, Long> genre_numberOfReleasesInDecade(int years, @NotNull String genre) {
        log.info("Getting releases of genre ({}) released in years ({})", genre, musicbrainz.getYearsFormatted(years));
        return musicbrainz.getNumberOfReleasesOfGenreInYears(years, genre);
    }

    public Map<String, Long> popularityOfGenres() {
        log.info("Getting popularity of genres");
        return lastFM.getPopularityOfGenres();
    }

    public Map<String, Integer> artist_popularityOfAlbums(@NotNull String artist) {
        log.info("Getting popularity of albums of artist {}", artist);
        return spotify.getPopularityOfAlbums(artist);
    }

    public Map<String, Integer> artist_popularityOfTracks(@NotNull String artist) {
        log.info("Getting popularity of tracks of artist {}", artist);
        return spotify.getPopularityOfTracksOfArtist(artist);
    }

    public Map<String, Long> album_durationOfTracks(@NotNull String album) {
        log.info("Getting duration of tracks of album {}", album);
        return spotify.getDurationOfTracksInAlbum(album);
    }

    public Map<String, Integer> album_popularityOfTracks(@NotNull String album) {
        log.info("Getting popularity of tracks from album {}", album);
        return spotify.getPopularityOfTracksInAlbum(album);
    }

    public Map<Integer, List<String>> artist_activity(@NotNull String artist) {
        log.info("Getting activity of artist {}", artist);
        return spotify.getActivityOfArtist(artist);
    }

    public Map<String, Long> genre_popularityOfArtists(@NotNull String genre) {
        log.info("Getting popular artists of genre {}", genre);
        return spotify.getPopularArtistsOfGenre(genre);
    }

    public void closeHttpClient() {
        requestService.closeHttpClient();
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
    }
}