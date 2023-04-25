package com.vance.lib;

import com.vance.lib.integration.LastFmIntegration;
import com.vance.lib.integration.MusicbrainzIntegration;
import com.vance.lib.integration.SpotifyIntegration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ChartDataProvider {
    private final Logger log = LoggerFactory.getLogger(ChartDataProvider.class);
    private final MusicbrainzIntegration musicbrainz = new MusicbrainzIntegration();
    private final SpotifyIntegration spotify = new SpotifyIntegration();
    private final LastFmIntegration lastFM = new LastFmIntegration();

    public Map<String, Long> numberOfArtistsOfGenres(@NotNull List<String> genres) {
        log.info("Getting number of artists of genres, genres: {}", genres);
        return musicbrainz.getNumberOfArtistsOfGenres(genres);
    }

    public Map<String, Long> numberOfReleasesOfGenres(@NotNull List<String> genres) {
        log.info("Getting number of releases of genres, genres: {}", genres);
        return musicbrainz.getNumberOfReleasesOfGenres(genres);
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
        MusicbrainzIntegration integration = new MusicbrainzIntegration();

        integration.getNumberOfReleasesOfGenres(List.of("rock"));
    }
}