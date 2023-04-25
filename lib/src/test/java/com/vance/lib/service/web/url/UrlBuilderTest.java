package com.vance.lib.service.web.url;

import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UrlBuilderTest {
    final String DISCOGS_BASE_URL = "https://api.discogs.com";
    final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1";
    final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";
    final String MUSICBRAINZ_BASE_URL = "https://musicbrainz.org/ws/2";
    final String LASTFM_BASE_URL = "http://ws.audioscrobbler.com/2.0";

    private final UrlBuilder builder = new UrlBuilder();


    @Test
    void shouldBuildBaseUrls() {
        // when
        final String discogs = builder.discogs().build();
        final String spotifyToken = builder.spotifyToken();
        final String spotify = builder.spotify().build();
        final String lastfm = builder.lastfm().build();
        final String musicbrainz = builder.musicbrainz().build();

        //then
        assertEquals(DISCOGS_BASE_URL, discogs);
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
}