package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Spliterator;

import static java.util.Map.entry;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

public class LastFmParser {
    private final ObjectMapper parser = new ObjectMapper();

    public Map<String, Long> parsePopularityOfGenres(@NotNull String genres) throws JsonProcessingException {
        final JsonNode parsedGenres = parser.readTree(genres).get("tags").get("tag");

        if (!parsedGenres.isArray())
            throw new RuntimeException("Cannot parse genres");

        return stream(spliteratorUnknownSize(parsedGenres.iterator(), Spliterator.ORDERED), true)
                .map(node -> entry(node.get("name").asText(), node.get("reach").asLong()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
