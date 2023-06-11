package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

public class SpotifyParser {
    private final Logger log = LoggerFactory.getLogger(SpotifyParser.class);
    private final String ITEMS = "items";
    private final String TRACKS = "tracks";
    private final ElementExtractor<JsonNode, String> ID_EXTRACTOR = node -> node.get("id").asText();
    private final ElementExtractor<JsonNode, Map.Entry<String, Long>> NAME_DURATION_EXTRACTOR =
            node -> Map.entry(node.get("name").asText(), node.get("duration_ms").asLong());
    private final ElementExtractor<JsonNode, Map.Entry<String, Integer>> NAME_POPULARITY_EXTRACTOR =
            node -> Map.entry(node.get("name").asText(), node.get("popularity").asInt());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String parseIdOfItem(@NotNull String item, SpotifySearchTypes type, @NotNull String itemName) throws JsonProcessingException {
        final String searchType = type.name().toLowerCase();
        final JsonNode parsedSearch = readTree(item);

        final int total = parsedSearch.get(searchType + "s").get("total").asInt();
        if (total == 0) throw new IllegalStateException(format("No %s to parse", searchType + "s"));

        final JsonNode parsedItem = parsedSearch.get(searchType + "s").get(ITEMS).get(0);
        if (!StringUtils.containsIgnoreCase(parsedItem.get("name").asText(), itemName))
            throw new IllegalStateException(format("Cannot find expected %s %s, found: %s", searchType, itemName, parsedItem.get("name").asText()));

        final String id = parsedItem.get("id").asText();
        log.debug("Parsed id of {} : {}", searchType, id);
        return id;
    }

    public String parseIdOfItems(@NotNull String items, SpotifySearchTypes type) throws JsonProcessingException {
        final JsonNode parsedItems = readTree(items).get(type.name().toLowerCase() + "s").get(ITEMS);

        if (!parsedItems.isArray()) throw new RuntimeException("Cannot parse ids of " + type);

        log.debug("Parsing id of items ({})", type.name().toLowerCase());
        return stream(spliteratorUnknownSize(parsedItems.iterator(), Spliterator.ORDERED), true)
                .map(ID_EXTRACTOR)
                .collect(joining(","));
    }

    public Map<String, Long> parseDurationOfTracksFromAlbum(@NotNull String tracks) throws JsonProcessingException {
        return stream(spliteratorUnknownSize(parseJsonArray(tracks, ITEMS).iterator(), Spliterator.ORDERED), true)
                .map(NAME_DURATION_EXTRACTOR)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Integer> parsePopularityOfAlbums(@NotNull String albums) throws JsonProcessingException {
        final Map<String, Integer> result = new HashMap<>();
        stream(spliteratorUnknownSize(parseJsonArray(albums, "albums").iterator(), Spliterator.ORDERED), true)
                .forEach(node -> result.put(node.get("name").asText(), node.get("popularity").asInt()));
        return result;
    }

    public Map<String, Integer> parsePopularityOfTracksOfArtist(@NotNull String tracks) throws JsonProcessingException {
        return stream(spliteratorUnknownSize(parseJsonArray(tracks, TRACKS).iterator(), Spliterator.ORDERED), true)
                .map(NAME_POPULARITY_EXTRACTOR)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Integer> parsePopularityOfTracksFromAlbum(String tracks) throws JsonProcessingException {
        return stream(spliteratorUnknownSize(parseJsonArray(tracks, TRACKS).iterator(), Spliterator.ORDERED), true)
                .map(NAME_POPULARITY_EXTRACTOR)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String parseIdOfTracks(String tracks) throws JsonProcessingException {
        return stream(spliteratorUnknownSize(parseJsonArray(tracks, ITEMS).iterator(), Spliterator.ORDERED), true)
                .map(ID_EXTRACTOR)
                .collect(joining(","));
    }

    public Map<Integer, List<String>> parseActivityOfArtist(String albums) throws JsonProcessingException {
        final Map<Integer, List<String>> result = new LinkedHashMap<>();
        stream(spliteratorUnknownSize(parseJsonArray(albums, ITEMS).iterator(), Spliterator.ORDERED), true)
                .forEach(node -> setArtistActivity(node, result));
        return result;
    }

    public Map<String, Long> parsePopularityOfArtistsOfGenre(String artists) throws JsonProcessingException {
        final String data = readTree(artists).get("artists").toString();
        return stream(spliteratorUnknownSize(parseJsonArray(data, ITEMS).iterator(), Spliterator.ORDERED), true)
                .map(node -> Map.entry(node.get("name").asText(), node.get("followers").get("total").asLong()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private JsonNode parseJsonArray(String json, String arrayName) throws JsonProcessingException {
        final JsonNode parsedArray = ofNullable(readTree(json).get(arrayName)).orElseThrow(() ->
                new RuntimeException(format("Cannot parse %s as it is not an array of nodes", arrayName)));

        if (!parsedArray.isArray())
            throw new RuntimeException(format("Cannot parse %s as it is not an array of nodes", arrayName));

        return parsedArray;
    }

    @FunctionalInterface
    private interface ElementExtractor<T extends JsonNode, R> extends Function<T, R> {
        R apply(T t);
    }

    private void setArtistActivity(JsonNode node, Map<Integer, List<String>> result) {
        final Integer year = parseInt(node.get("release_date").asText().split("-")[0]);
        final String releaseNameAndType = format("%s : %s", node.get("name").asText(), node.get("album_type").asText());

        if (result.containsKey(year)) {
            if (!requireNonNull(result.get(year)).contains(releaseNameAndType))
                requireNonNull(result.get(year)).add(releaseNameAndType);
        } else {
            List<String> list = new ArrayList<>();
            list.add(releaseNameAndType);
            result.put(year, list);
        }
    }

    private JsonNode readTree(String data) throws JsonProcessingException {
        try {
            return objectMapper.readTree(data);
        } catch (NullPointerException e) {
            throw new RuntimeException("Parsing error", e);
        }
    }
}
