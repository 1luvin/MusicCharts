package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpotifyParser {
    private final Logger log = LoggerFactory.getLogger(SpotifyParser.class);
    private final String ITEMS_ARRAY_NAME = "items";
    private final String TRACKS_ARRAY_NAME = "tracks";
    private final String ALBUMS_ARRAY_NAME = "albums";
    private final ElementExtractor<JsonNode, String> ID_EXTRACTOR =
            node -> node.get("id").asText();
    private final ElementExtractor<JsonNode, Map.Entry<String, Long>> NAME_DURATION_EXTRACTOR =
            node -> Map.entry(node.get("name").asText(), node.get("duration_ms").asLong());
    private final ElementExtractor<JsonNode, Map.Entry<String, Integer>> NAME_POPULARITY_EXTRACTOR =
            node -> Map.entry(node.get("name").asText(), node.get("popularity").asInt());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String parseIdOfItem(@NotNull String item, SpotifySearchTypes type) throws JsonProcessingException {
        final String searchType = type.name().toLowerCase();
        final String id = objectMapper.readTree(item).get(searchType + "s")
                .get(ITEMS_ARRAY_NAME)
                .get(0)
                .get("id").asText();
        log.debug(String.format("Parsed %s : %s", searchType, id));
        return id;
    }

    public String parseIdOfItems(@NotNull String items, SpotifySearchTypes type) throws JsonProcessingException {
        final JsonNode parsedItems = objectMapper.readTree(items)
                .get(type.name().toLowerCase() + "s")
                .get(ITEMS_ARRAY_NAME);

        if (!parsedItems.isArray())
            throw new RuntimeException("Cannot parse ids of " + type);
        log.debug("Parsing id of items");
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(parsedItems.iterator(), Spliterator.ORDERED), true)
                .map(ID_EXTRACTOR)
                .collect(Collectors.joining(","));
    }

    public Map<String, Long> parseDurationOfTracksFromAlbum(@NotNull String albumTracks) throws JsonProcessingException {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        parseJsonArray(albumTracks, ITEMS_ARRAY_NAME).iterator(), Spliterator.ORDERED), true)
                .map(NAME_DURATION_EXTRACTOR)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Integer> parsePopularityOfAlbums(@NotNull String albums) throws JsonProcessingException {
        Map<String, Integer> result = new HashMap<>();
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        parseJsonArray(albums, ALBUMS_ARRAY_NAME).iterator(), Spliterator.ORDERED), true)
                .map(NAME_POPULARITY_EXTRACTOR)
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    public Map<String, Integer> parsePopularityOfTracks(@NotNull String tracks) throws JsonProcessingException {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        parseJsonArray(tracks, TRACKS_ARRAY_NAME).iterator(), Spliterator.ORDERED), true)
                .map(NAME_POPULARITY_EXTRACTOR)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Integer> parsePopularityOfTracksFromAlbum(String tracks) throws JsonProcessingException {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        parseJsonArray(tracks, TRACKS_ARRAY_NAME).iterator(), Spliterator.ORDERED), true)
                .map(NAME_POPULARITY_EXTRACTOR)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String parseIdOfTracks(String tracks) throws JsonProcessingException {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        parseJsonArray(tracks, ITEMS_ARRAY_NAME).iterator(), Spliterator.ORDERED), true)
                .map(ID_EXTRACTOR)
                .collect(Collectors.joining(","));

    }

    private JsonNode parseJsonArray(String json, String arrayName) throws JsonProcessingException {
        final JsonNode parsedArray = objectMapper.readTree(json).get(arrayName);
        if (!parsedArray.isArray())
            throw new RuntimeException(String.format("Cannot parse %s as it is not an array of nodes", arrayName));
        log.debug(String.format("Parsed array of %s : %s", arrayName, parsedArray.asText()));
        return parsedArray;
    }

    @FunctionalInterface
    private interface ElementExtractor<T extends JsonNode, R> extends Function<T, R> {
        R apply(T t);
    }
}
