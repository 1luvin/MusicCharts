package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpotifyParserTest {
    private final SpotifyParser parser = new SpotifyParser();

    @Test
    void shouldParseIdOfItem() throws JsonProcessingException {
        // given
        final String json = "{ \"artists\": { \"items\" : [{ \"id\" : \"TEST_ID\" }], \"total\" : \"1\" } }";
        final String expectedID = "TEST_ID";

        // when
        final String actualID = parser.parseIdOfItem(json, SpotifySearchTypes.ARTIST);

        // then
        assertEquals(expectedID, actualID);
    }

    @Test
    void shouldParseIdOfItems() throws JsonProcessingException {
        // given
        final String json = "{ \"albums\": { \"items\" : [{ \"id\" : \"TEST_ID_1\" }, {\"id\" : \"TEST_ID_2\"}] } }";
        final String expectedIDs = "TEST_ID_1,TEST_ID_2";

        // when
        final String actualIDs = parser.parseIdOfItems(json, SpotifySearchTypes.ALBUM);

        // then
        assertEquals(expectedIDs, actualIDs);
    }

    @Test
    void shouldParseIdOfTracks() throws JsonProcessingException {
        // given
        final String json = "{ \"items\" : [{ \"id\" : \"TEST_ID_1\" }, {\"id\" : \"TEST_ID_2\"}] }";
        final String expectedIDs = "TEST_ID_1,TEST_ID_2";

        // when
        final String actualIDs = parser.parseIdOfTracks(json);

        // then
        assertEquals(expectedIDs, actualIDs);
    }

    @Test
    void shouldParseActivityOfArtist() throws JsonProcessingException {
        // given
        final String json = "{ \"items\" : [{ \"release_date\" : \"2000-01-23\", " +
                "\"name\" : \"TEST_1\", \"album_type\" : \"album\" }, " +
                "{ \"release_date\" : \"2001-02-15\", \"name\" : \"TEST_2\", " +
                "\"album_type\" : \"album\" }]}";
        final int expectedSize = 2;
        final List<String> firstList = List.of("TEST_1 : album");
        final List<String> secondList = List.of("TEST_2 : album");

        // when
        final Map<Integer, List<String>> actualActivity = parser.parseActivityOfArtist(json);

        // then
        assertEquals(expectedSize, actualActivity.size());
        assertNotNull(actualActivity.get(2000));
        assertNotNull(actualActivity.get(2001));
        assertEquals(firstList.get(0), actualActivity.get(2000).get(0));
        assertEquals(secondList.get(0), actualActivity.get(2001).get(0));
    }

    @Test
    void shouldParseDurationOfTracksFromAlbum() throws JsonProcessingException {
        // given
        final String json = "{" +
                "  \"items\" : [" +
                "  { \"duration_ms\" : 123, \"name\" : \"TEST_1\"}, " +
                "  { \"duration_ms\" : 456, \"name\" : \"TEST_2\"}" +
                "  ]" +
                "}";
        final long firstDuration = 123L;
        final long secondDuration = 456L;
        final String firstName = "TEST_1";
        final String secondName = "TEST_2";
        final int expectedSize = 2;

        // when
        final Map<String, Long> result = parser.parseDurationOfTracksFromAlbum(json);

        // then
        assertEquals(expectedSize, result.size());
        assertNotNull(result.get(firstName));
        assertNotNull(result.get(secondName));
        assertEquals(firstDuration, result.get(firstName));
        assertEquals(secondDuration, result.get(secondName));
    }

    @Test
    void shouldParsePopularityOfAlbums() throws JsonProcessingException {
        // given
        final String json = "{" +
                "  \"albums\" : [ " +
                "   { \"name\" : \"TEST_1\", \"popularity\" : 1 }," +
                "   { \"name\" : \"TEST_2\", \"popularity\" : 2 }" +
                "  ]" +
                "}";
        final int expectedSize = 2;
        final int firstPopularity = 1;
        final int secondPopularity = 2;
        final String firstName = "TEST_1";
        final String secondName = "TEST_2";

        // when
        final Map<String, Integer> result = parser.parsePopularityOfAlbums(json);

        // then
        assertEquals(expectedSize, result.size());
        assertNotNull(result.get(firstName));
        assertNotNull(result.get(secondName));
        assertEquals(firstPopularity, result.get(firstName));
        assertEquals(secondPopularity, result.get(secondName));
    }

    @Test
    void shouldParsePopularityOfTracks() throws JsonProcessingException {
        // given
        final String json = "{" +
                "  \"tracks\" : [ " +
                "   {\"name\" : \"TEST_1\", \"popularity\" : 1}," +
                "   {\"name\" : \"TEST_2\", \"popularity\" : 2}" +
                "  ]" +
                "}";
        final int expectedSize = 2;
        final String firstName = "TEST_1";
        final String secondName = "TEST_2";
        final int firstPopularity = 1;
        final int secondPopularity = 2;

        // when
        final Map<String, Integer> result = parser.parsePopularityOfTracksFromAlbum(json);

        // then
        assertEquals(expectedSize, result.size());
        assertNotNull(result.get(firstName));
        assertNotNull(result.get(secondName));
        assertEquals(firstPopularity, result.get(firstName));
        assertEquals(secondPopularity, result.get(secondName));
    }

    @Test
    void shouldNotParseAnythingFromEmptyJSON() {
        // given
        final String emptyJSON = "";

        // when & then
        assertThrows(RuntimeException.class, () -> parser.parseIdOfTracks(emptyJSON));
        assertThrows(RuntimeException.class, () -> parser.parseActivityOfArtist(emptyJSON));
        assertThrows(RuntimeException.class, () -> parser.parsePopularityOfAlbums(emptyJSON));
        assertThrows(RuntimeException.class, () -> parser.parsePopularityOfTracks(emptyJSON));
        assertThrows(RuntimeException.class, () -> parser.parsePopularityOfTracksFromAlbum(emptyJSON));
        assertThrows(NullPointerException.class, () -> parser.parseIdOfItem(emptyJSON, SpotifySearchTypes.ALBUM));
        assertThrows(NullPointerException.class, () -> parser.parseIdOfItems(emptyJSON, SpotifySearchTypes.ALBUM));
        assertThrows(NullPointerException.class, () -> parser.parsePopularityOfArtistsOfGenre(emptyJSON));
    }
}