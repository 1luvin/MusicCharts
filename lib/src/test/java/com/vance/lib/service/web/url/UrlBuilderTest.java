package com.vance.lib.service.web.url;

import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlBuilderTest {
    private final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1";
    private final String MUSICBRAINZ_BASE_URL = "https://musicbrainz.org/ws/2";
    private final String LASTFM_BASE_URL = "http://ws.audioscrobbler.com/2.0";
    private final String TEST_GENRE = "TEST_GENRE";
    private final UrlBuilder builder = new UrlBuilder();


    @Test
    void shouldBuildBaseUrls() {
        // given
        final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";

        // when
        final String spotifyToken = builder.spotifyToken();
        final String spotify = builder.spotify().build();
        final String lastfm = builder.lastfm().build();
        final String musicbrainz = builder.musicbrainz().build();

        //then
        assertEquals(SPOTIFY_TOKEN_URL, spotifyToken);
        assertEquals(SPOTIFY_BASE_URL, spotify);
        assertEquals(MUSICBRAINZ_BASE_URL, musicbrainz);
        assertEquals(LASTFM_BASE_URL, lastfm);
    }


    // Spotify URLs tests:
    @Test
    void shouldBuildSpotifyUrlForArtist() {
        // given
        final String artistId = "TEST_ARTIST_ID";
        final String expectedUrl = SPOTIFY_BASE_URL + "/artists/" + artistId;

        // when
        final String actualUrl = builder.spotify()
                .artist(artistId)
                .build();

        // then
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void shouldBuildSpotifyUrlForAlbumsOfArtist() {
        // given
        final String artistId = "TEST_ARTIST_ID";
        final String expectedUrl = SPOTIFY_BASE_URL + "/artists/" + artistId + "/albums?include_groups=album,single,appears_on,compilation&limit=50";

        // when
        final String actualUrl = builder.spotify()
                .albumsOfArtist(artistId, false)
                .build();

        // then
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void shouldBuildSpotifyUrlForAlbum() {
        // given
        final String albumId = "TEST_ALBUM_ID";
        final String expectedUrl = SPOTIFY_BASE_URL + "/albums/" + albumId;

        // when
        final String actualUrl = builder.spotify()
                .album(albumId)
                .build();

        // then
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void shouldBuildSpotifyUrlForTracksOfAlbum() {
        // given
        final String albumId = "TEST_ALBUM_ID";
        final String expectedUrl = SPOTIFY_BASE_URL + "/albums/" + albumId + "/tracks";

        // when
        final String actualUrl = builder.spotify()
                .albumTracks(albumId)
                .build();

        // then
        assertEquals(expectedUrl, actualUrl);

    }

    @Test
    void shouldBuildSpotifySearchUrl() {
        // given
        final String query = "Nirvana";
        final String type = "artist";
        final String expectedUrl = SPOTIFY_BASE_URL + "/search?q=" + query + "&type=" + type;

        // when
        final String actualUrl = builder.spotify()
                .search(query, SpotifySearchTypes.ARTIST, false)
                .build();

        // then
        assertEquals(expectedUrl, actualUrl);
    }

    // Musicbrainz URLs tests:

    @Test
    void shouldBuildUrlForNumberOfArtistsOfGenre() {
        // given
        final String expectedUrl = MUSICBRAINZ_BASE_URL + "/artist/?query=type:group%20AND%20tag:" + TEST_GENRE;

        // when
        final String actualUrl = builder.musicbrainz()
                .numberOfArtistsOfGenre(TEST_GENRE)
                .build();

        // then
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void shouldBuildUrlForReleasesOfGenre() {
        // given
        final String expectedUrl = MUSICBRAINZ_BASE_URL + "/release/?query=tag:" + TEST_GENRE;

        // when
        final String actualUrl = builder.musicbrainz()
                .releasesOfGenre(TEST_GENRE)
                .build();

        // then
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void shouldBuildUrlForReleasesOfGenreInYears() {
        // given
        final String date = "2003";
        final String expectedUrl = MUSICBRAINZ_BASE_URL + "/release/?query=date:" + date + "%20AND%20tag:" + TEST_GENRE;

        // when
        final String actualUrl = builder.musicbrainz()
                .releasesOfGenre(TEST_GENRE, date)
                .build();

        // then
        assertEquals(expectedUrl, actualUrl);
    }

    // Last FM url tests:

    @Test
    void shouldBuildUrlForTopGenres() {
        // given
        final String expectedUrl = LASTFM_BASE_URL + "?method=chart.gettoptags";

        // when
        final String actualUrl = builder.lastfm()
                .getTopGenres()
                .build();

        // then
        assertEquals(expectedUrl, actualUrl);
    }
}