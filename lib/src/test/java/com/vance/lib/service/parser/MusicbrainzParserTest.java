package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MusicbrainzParserTest {
    private final MusicbrainzParser parser = new MusicbrainzParser();

    @Test
    void shouldParseCountOfItems() throws JsonProcessingException {
        // given
        final String json = "{ \"count\":123}";
        final long expected = 123L;

        // when
        final Long actual = parser.parseCountOfItems(json);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldNotParseCountOfItems() {
        // given
        final String json = "";

        // when & then
        assertThrows(RuntimeException.class, () -> parser.parseCountOfItems(json));
    }
}