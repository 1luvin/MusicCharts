package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LastFmParser {
    private final ObjectMapper parser = new ObjectMapper();

    public Map<String, Long> parsePopularityOfGenres(@NotNull String genres) throws JsonProcessingException {
        JsonNode parsedGenres = parser.readTree(genres).get("tags").get("tag");
        if (!parsedGenres.isArray())
            throw new RuntimeException("Cannot parse genres");
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(parsedGenres.iterator(), Spliterator.ORDERED), true)
                .map(node -> Map.entry(node.get("name").asText(), node.get("reach").asLong()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
