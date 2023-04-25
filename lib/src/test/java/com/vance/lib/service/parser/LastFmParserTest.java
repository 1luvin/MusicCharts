package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LastFmParserTest {

    private final LastFmParser parser = new LastFmParser();

    @Test
    void shouldParsePopularityOfGenres() throws JsonProcessingException {
        // given
        final String jsonInput = "{ \"tags\": { \"tag\": [{\"name\":\"rock\", \"reach\":\"10\"}] }}";
        final int expectedSize = 1;

        // when
        final Map<String, Long> result = parser.parsePopularityOfGenres(jsonInput);

        // then
        assertEquals(expectedSize, result.size());
        assertEquals("rock", result.keySet().toArray()[0]);
        assertEquals(10L, result.get("rock"));
    }

    @Test
    void shouldThrowNullPointerException() {
        // given
        final String jsonInput = "";

        // when and then
        assertThrows(NullPointerException.class, () -> parser.parsePopularityOfGenres(jsonInput));
    }

    @Test
    void shouldThrowProcessingException() {
        // given
        final String jsonInput = "1234";

        // when and then
        assertThrows(NullPointerException.class, () -> parser.parsePopularityOfGenres(jsonInput));
    }

    @Test
    void shouldThrowRuntimeException() {
        // given
        final String jsonInput = "{ \"tags\": { \"tag\": {} }}";

        // when and then
        assertThrows(RuntimeException.class, () -> parser.parsePopularityOfGenres(jsonInput));
    }
}