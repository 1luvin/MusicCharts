package com.vance.lib.service.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlBuilderTest {

    private final UrlBuilder builder = new UrlBuilder();

    @Test
    void shouldBuildBaseUrls() {
        // given
        final String DISCOGS_BASE_URL = "https://api.discogs.com";
        final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1";
        final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";
        final String MUSICBRAINZ_BASE_URL = "https://musicbrainz.org/ws/2";
        final String LASTFM_BASE_URL = " http://ws.audioscrobbler.com/2.0";

        // when
        final String discogs = builder.discogs().build();
        final String spotifyToken = builder.spotifyToken().build();
        final String spotify = builder.spotify().build();
        final String lastfm = builder.lastfm().build();
        final String musicbrainz = builder.musicbrainz().build();

        //then
        assertEquals(DISCOGS_BASE_URL, discogs);
        assertEquals(SPOTIFY_BASE_URL, spotifyToken);
        assertEquals(SPOTIFY_TOKEN_URL, spotify);
        assertEquals(MUSICBRAINZ_BASE_URL, lastfm);
        assertEquals(LASTFM_BASE_URL, musicbrainz);
    }
}