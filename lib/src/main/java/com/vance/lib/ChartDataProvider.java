package com.vance.lib;

import com.vance.lib.integration.LastFmIntegration;
import com.vance.lib.integration.MusicbrainzIntegration;
import com.vance.lib.integration.SpotifyIntegration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ChartDataProvider {
    private final SpotifyIntegration spotify = new SpotifyIntegration();
    private final MusicbrainzIntegration musicbrainz = new MusicbrainzIntegration();
    private final LastFmIntegration lastFM = new LastFmIntegration();

    public Map<String, Long> numberOfArtistsOfGenres(@NotNull List<String> genres) {
        return musicbrainz.getNumberOfArtistsOfGenres(genres);
    }

    public Map<String, Long> numberOfReleasesOfGenres(@NotNull List<String> genres) {
        return musicbrainz.getNumberOfReleasesOfGenres(genres);
    }

    public Map<String, Long> numberOfReleasesOfGenreInYears(int years, @NotNull String genre) {
        return musicbrainz.getNumberOfReleasesOfGenreInYears(years, genre);
    }

    public Map<String, Long> popularityOfGenres() {
        return lastFM.getPopularityOfGenres();
    }

    public Map<String, Integer> popularityOfAlbums(@NotNull String artist) {
        return spotify.getPopularityOfAlbums(artist);
    }

    public Map<String, Integer> popularityOfTracksOfArtist(@NotNull String artist) {
        return spotify.getPopularityOfTracksOfArtist(artist);
    }

    public Map<String, Long> durationOfTracksInAlbum(@NotNull String album) {
        return spotify.getDurationOfTracksInAlbum(album);
    }

    public Map<String, Integer> popularityOfTracksInAlbum(@NotNull String album) {
        return spotify.getPopularityOfTracksInAlbum(album);
    }

    public Map<Integer, List<String>> activityOfArtist(@NotNull String artist) {
        return spotify.getActivityOfArtist(artist);
    }

    public Map<String, Long> popularArtistsOfGenre(@NotNull String genre) {
        return spotify.getPopularArtistsOfGenre(genre);
    }
}