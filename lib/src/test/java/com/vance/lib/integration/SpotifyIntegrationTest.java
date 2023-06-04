package com.vance.lib.integration;

import com.vance.lib.service.parser.SpotifyParser;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.secrets.SecretProvider;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.vance.lib.util.FileUtil.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class SpotifyIntegrationTest {

    private static final RequestService requestService = mock(RequestService.class);
    private static final SecretProvider secretProvider = mock(SecretProvider.class);
    private final SpotifyParser parser = new SpotifyParser();

    private final SpotifyIntegration spotifyIntegration = new SpotifyIntegration(requestService, secretProvider);

    @BeforeAll
    static void setUp() {
        given(secretProvider.getSpotifyToken()).willReturn("TEST_TOKEN");
    }

    @Test
    void shouldGetPopularityOfAlbums() throws IOException {
        // given
        final String albums = readFile("spotify/albumsSearch.json", SpotifyIntegrationTest.class);
        final String popularity = readFile("spotify/albumsPopularity.json", SpotifyIntegration.class);
        final Map<String, Integer> expected = parser.parsePopularityOfAlbums(popularity);

        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(albums, popularity);

        // when
        final Map<String, Integer> actual = spotifyIntegration.getPopularityOfAlbums("TEST");

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldNotGetPopularityOfAlbumsAsAlbumsSearchResponseIsEmpty() {
        // given
        final String albums = "{}";
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(albums);

        // when & then
        assertThrows(NullPointerException.class, () -> spotifyIntegration.getPopularityOfAlbums("TEST"));
    }

    @Test
    void shouldNotGetPopularityOfAlbumsAsAlbumsPopularityResponseIsEmpty() throws IOException {
        // given
        final String albums = readFile("spotify/albumsSearch.json", SpotifyIntegrationTest.class);
        final String popularity = "{}";

        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(albums, popularity);

        // when & then
        assertThrows(RuntimeException.class, () -> spotifyIntegration.getPopularityOfAlbums("TEST"));
    }

    @Test
    void shouldNotGetPopularityOfAlbumsAsAlbumsSearchResponseIsBad() {
        // given
        final String albums = "{---}";
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(albums);

        // when & then
        assertThrows(SpotifyIntegration.SpotifyIntegrationException.class,
                () -> spotifyIntegration.getPopularityOfAlbums("TEST"));
    }

    @Test
    void shouldNotGetPopularityOfAlbumsAsAlbumPopularityIsBad() throws IOException {
        // given
        final String albums = readFile("spotify/albumsSearch.json", SpotifyIntegrationTest.class);
        final String popularity = "{---}";
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(albums, popularity);

        // when & then
        assertThrows(SpotifyIntegration.SpotifyIntegrationException.class,
                () -> spotifyIntegration.getPopularityOfAlbums("TEST"));
    }

    @Test
    void shouldGetActivityOfArtist() throws IOException {
        // given
        final String artist = readFile("spotify/artistSearch.json", SpotifyIntegrationTest.class);
        final String albums = readFile("spotify/albumsOfArtist.json", SpotifyIntegrationTest.class);
        final Map<Integer, List<String>> expected = parser.parseActivityOfArtist(albums);
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(artist, albums);

        // when
        final Map<Integer, List<String>> actual = spotifyIntegration.getActivityOfArtist("Deftones");

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldNotGetActivityOfArtistAsArtistNameIsWrong() throws IOException {
        // given
        final String artist = readFile("spotify/artistSearch.json", SpotifyIntegrationTest.class);
        final String albums = readFile("spotify/albumsOfArtist.json", SpotifyIntegrationTest.class);
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(artist, albums);

        // when & then
        assertThrows(IllegalStateException.class, () -> spotifyIntegration.getActivityOfArtist("WRONG_NAME"));
    }

    @Test
    void shouldGetDurationOfTracksInAlbum() throws IOException {
        // given
        final String album = readFile("spotify/albumSearch.json", SpotifyIntegrationTest.class);
        final String tracks = readFile("spotify/tracksOfAlbum.json", SpotifyIntegrationTest.class);
        final Map<String, Long> expected = parser.parseDurationOfTracksFromAlbum(tracks);
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(album, tracks);


        // when
        final Map<String, Long> actual = spotifyIntegration.getDurationOfTracksInAlbum("White Pony");

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldGetPopularityOfTracksInAlbum() throws IOException {
        // given
        final String album = readFile("spotify/albumSearch.json", SpotifyIntegrationTest.class);
        final String tracks = readFile("spotify/tracksOfAlbum.json", SpotifyIntegrationTest.class);
        final String popularity = readFile("spotify/popularityOfTracks.json", SpotifyIntegrationTest.class);
        final Map<String, Integer> expected = parser.parsePopularityOfTracksFromAlbum(popularity);

        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(album, tracks, popularity);

        // when
        final Map<String, Integer> actual = spotifyIntegration.getPopularityOfTracksInAlbum("White Pony");

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldGet() throws IOException {
        // given
        final String artist = readFile("spotify/artistSearch.json", SpotifyIntegrationTest.class);
        final String tracks = readFile("spotify/tracksOfArtist.json", SpotifyIntegrationTest.class);
        final Map<String, Integer> expected = parser.parsePopularityOfTracksOfArtist(tracks);

        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(artist, tracks);

        // when
        final Map<String, Integer> actual = spotifyIntegration.getPopularityOfTracksOfArtist("Deftones");

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldGetPopularArtistsOfGenre() throws IOException {
        // given
        final String artists = readFile("spotify/popularArtists.json", SpotifyIntegrationTest.class);
        final Map<String, Long> expected = parser.parsePopularityOfArtistsOfGenre(artists);
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(artists);

        // when
        final Map<String, Long> actual = spotifyIntegration.getPopularArtistsOfGenre("Rock");

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldFailInAnyActionForBadHttpResponseBody() {
        // given
        final String testArgument = "TEST";
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn("BAD RESPONSE");


        // when & then
        assertThrows(SpotifyIntegration.SpotifyIntegrationException.class,
                () -> spotifyIntegration.getActivityOfArtist(testArgument));
        assertThrows(SpotifyIntegration.SpotifyIntegrationException.class,
                () -> spotifyIntegration.getPopularityOfAlbums(testArgument));
        assertThrows(SpotifyIntegration.SpotifyIntegrationException.class,
                () -> spotifyIntegration.getPopularArtistsOfGenre(testArgument));
        assertThrows(SpotifyIntegration.SpotifyIntegrationException.class,
                () -> spotifyIntegration.getDurationOfTracksInAlbum(testArgument));
        assertThrows(SpotifyIntegration.SpotifyIntegrationException.class,
                () -> spotifyIntegration.getPopularityOfTracksInAlbum(testArgument));
        assertThrows(SpotifyIntegration.SpotifyIntegrationException.class,
                () -> spotifyIntegration.getPopularityOfTracksOfArtist(testArgument));
    }
}