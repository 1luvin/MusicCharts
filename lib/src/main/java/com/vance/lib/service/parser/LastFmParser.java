package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Spliterator;

import static com.vance.lib.util.JsonUtil.*;
import static java.util.Map.entry;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

public class LastFmParser {

    public Map<String, Long> parsePopularityOfGenres(@NotNull String genres) throws JsonProcessingException, ParsingException {
        final JsonNode parsedGenres = readTree(genres).get("tags").get("tag");

        if (!parsedGenres.isArray()) throw new ParsingException("Cannot parse genres");

        return stream(spliteratorUnknownSize(parsedGenres.iterator(), Spliterator.ORDERED), true)
                .map(node -> entry(getText(node, "name"), getLong(node, "reach")))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
