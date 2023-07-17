package com.vance.lib.integration;

import com.vance.lib.service.parser.MusicbrainzParser;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.util.FileUtil;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MusicbrainzIntegrationTest {

    private final RequestService requestService = Mockito.mock(RequestService.class);
    private final MusicbrainzParser parser = new MusicbrainzParser();

    private final MusicbrainzIntegration musicbrainzIntegration = new MusicbrainzIntegration(requestService);

//    @Test
    void shouldGetNumberOfArtistsOfGenres() throws IOException, IntegrationException {
        // given
        final String rock = "rock";
        final String hipHop = "hip-hop";
        final String metal = "metal";
        final List<String> genres = List.of(rock, hipHop, metal);

        final String rockResponse = readFile("musicbrainz/RockArtistsNumber.json");
        final String hipHopResponse = readFile("musicbrainz/HipHopArtistsNumber.json");
        final String metalResponse = readFile("musicbrainz/MetalArtistsNumber.json");

        given(requestService.sendRequest(any(ClassicHttpRequest.class)))
                .willReturn(rockResponse, hipHopResponse, metalResponse);

        final int expectedSize = 3;
        final long expectedRockArtists = parser.parseCountOfItems(rockResponse);
        final long expectedHipHopArtists = parser.parseCountOfItems(hipHopResponse);
        final long expectedMetalArtists = parser.parseCountOfItems(metalResponse);

        // when
        final Map<String, Long> actual = musicbrainzIntegration.getNumberOfArtistsOfGenres(metal);

        // then
        verify(requestService, times(3)).sendRequest(any(ClassicHttpRequest.class));
        assertEquals(expectedSize, actual.size());
        assertEquals(expectedRockArtists, actual.get(rock));
        assertEquals(expectedHipHopArtists, actual.get(hipHop));
        assertEquals(expectedMetalArtists, actual.get(metal));
    }

//    @Test
    void shouldGetNumberOfReleasesOfGenres() throws IOException, IntegrationException {
        // given
        final String rock = "rock";
        final String hipHop = "hip-hop";
        final String metal = "metal";

        final String rockResponse = readFile("musicbrainz/RockGenreReleases.json");
        final String hipHopResponse = readFile("musicbrainz/HipHopGenreReleases.json");
        final String metalResponse = readFile("musicbrainz/MetalGenreReleases.json");

        given(requestService.sendRequest(any(ClassicHttpRequest.class)))
                .willReturn(rockResponse, hipHopResponse, metalResponse);

        final int expectedSize = 3;
        final long expectedRockReleases = parser.parseCountOfItems(rockResponse);
        final long expectedHipHopReleases = parser.parseCountOfItems(hipHopResponse);
        final long expectedMetalReleases = parser.parseCountOfItems(metalResponse);

        // when
        final Map<String, Long> actual = musicbrainzIntegration.getNumberOfReleasesOfGenres(metal);

        // then
        verify(requestService, times(3)).sendRequest(any(ClassicHttpRequest.class));
        assertEquals(expectedSize, actual.size());
        assertEquals(expectedRockReleases, actual.get(rock));
        assertEquals(expectedHipHopReleases, actual.get(hipHop));
        assertEquals(expectedMetalReleases, actual.get(metal));
    }

    @Test
    void shouldGetNumberOfReleasesOfGenreInYears() throws IntegrationException {
        // given
        final int expectedSize = 10;
        final long expectedValue = 1234;

        final int YEARS_90_TO_99 = 0;
        final int YEARS_00_TO_09 = 1;
        final int YEARS_10_TO_19 = 2;
        final String genre = "rock";
        final String response = "{\"count\":1234}";
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(response);

        // when
        musicbrainzIntegration.setWaitTime(0);
        final Map<String, Long> actual1 = musicbrainzIntegration.getNumberOfReleasesOfGenreInYears(YEARS_90_TO_99, genre);
        final Map<String, Long> actual2 = musicbrainzIntegration.getNumberOfReleasesOfGenreInYears(YEARS_00_TO_09, genre);
        final Map<String, Long> actual3 = musicbrainzIntegration.getNumberOfReleasesOfGenreInYears(YEARS_10_TO_19, genre);

        // then
        verify(requestService, times(30)).sendRequest(any(ClassicHttpRequest.class));
        assertEquals(expectedSize, actual1.size());
        assertEquals(expectedSize, actual2.size());
        assertEquals(expectedSize, actual3.size());

        actual1.forEach((key, value) -> assertEquals(expectedValue, value,
                String.format("Value for the year %s does not match the expected", key)));
        actual2.forEach((key, value) -> assertEquals(expectedValue, value,
                String.format("Value for the year %s does not match the expected", key)));
        actual2.forEach((key, value) -> assertEquals(expectedValue, value,
                String.format("Value for the year %s does not match the expected", key)));
    }

    @Test
    void shouldNotGetNumberOfReleasesInYearForBadYearProvided() {
        // given
        final int badYear = 999;

        // when & then
        assertThrows(MusicbrainzIntegration.MusicbrainzIntegrationException.class,
                () -> musicbrainzIntegration.getNumberOfReleasesOfGenreInYears(badYear, "rock"));
    }

    @Test
    void shouldThrowMusicbrainzIntegrationException() {
        // given
        final int years = 0;
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn("---");

        // when & then
        assertThrows(MusicbrainzIntegration.MusicbrainzIntegrationException.class,
                () -> musicbrainzIntegration.getNumberOfReleasesOfGenreInYears(years, "rock"));
    }

    @Test
    void shouldThrowIllegalStateException() {
        // given
        final String badGenre = "/,.1231.....}[";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> musicbrainzIntegration.getNumberOfArtistsOfGenres(badGenre));
    }

    private String readFile(String fileName) throws IOException {
        return FileUtil.readFile(fileName, MusicbrainzIntegrationTest.class);
    }
}