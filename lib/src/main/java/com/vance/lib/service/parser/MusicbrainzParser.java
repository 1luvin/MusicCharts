package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MusicbrainzParser {
    private final ObjectMapper parser = new ObjectMapper();

    public Long parseCountOfItems(String info) throws JsonProcessingException {
        return parser.readTree(info).get("count").asLong();
    }
}
