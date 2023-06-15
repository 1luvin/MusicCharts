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

import static com.vance.lib.util.StringUtil.removeParts;
import static com.vance.lib.util.StringUtil.validateString;
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
    private final String ID = "id";
    private final String NAME = "name";
    private final String TOTAL = "total";
    private final String ITEMS = "items";
    private final String TRACKS = "tracks";
    private final String POPULARITY = "popularity";
    private final List<String> removableParts = List.of(" (Remastered)", "(Remastered)");
    private final List<String> restrictedValues = List.of(" (Live at", "Mix");
    private final ElementExtractor<JsonNode, String> ID_EXTRACTOR = node -> getText(node, ID);
    private final ElementExtractor<JsonNode, Map.Entry<String, Long>> NAME_DURATION_EXTRACTOR =
            node -> Map.entry(removeParts(getText(node, NAME), removableParts), getLong(node, "duration_ms"));
    private final ElementExtractor<JsonNode, Map.Entry<String, Integer>> NAME_POPULARITY_EXTRACTOR =
            node -> Map.entry(removeParts(getText(node, NAME), removableParts), getInt(node, POPULARITY));
    private final ObjectMapper objectMapper = new ObjectMapper();


    public String parseIdOfItem(@NotNull String item, SpotifySearchTypes type, @NotNull String itemName) throws JsonProcessingException {
        final String searchType = type.name().toLowerCase();
        final JsonNode parsedSearch = readTree(item);

        final int total = getInt(parsedSearch.get(searchType + "s"), TOTAL);
        if (total == 0) throw new IllegalStateException(format("No %s to parse", searchType + "s"));

        final JsonNode parsedItem = parsedSearch.get(searchType + "s").get(ITEMS).get(0);
        if (!StringUtils.containsIgnoreCase(getText(parsedItem, NAME), itemName))
            throw new IllegalStateException(format("Cannot find expected %s %s, found: %s", searchType, itemName, getText(parsedItem, NAME)));

        final String id = getText(parsedItem, ID);
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
                .filter(entry -> validateString(entry.getKey(), restrictedValues))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Integer> parsePopularityOfAlbums(@NotNull String albums) throws JsonProcessingException {
        final Map<String, Integer> result = new HashMap<>();
        stream(spliteratorUnknownSize(parseJsonArray(albums, "albums").iterator(), Spliterator.ORDERED), true)
                .forEach(node -> result.put(removeParts(getText(node, NAME), removableParts), getInt(node, POPULARITY)));
        return result;
    }

    public Map<String, Integer> parsePopularityOfTracksOfArtist(@NotNull String tracks) throws JsonProcessingException {
        return stream(spliteratorUnknownSize(parseJsonArray(tracks, TRACKS).iterator(), Spliterator.ORDERED), true)
                .map(NAME_POPULARITY_EXTRACTOR)
                .filter(entry -> validateString(entry.getKey(), restrictedValues))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Integer> parsePopularityOfTracksFromAlbum(String tracks) throws JsonProcessingException {
        return stream(spliteratorUnknownSize(parseJsonArray(tracks, TRACKS).iterator(), Spliterator.ORDERED), true)
                .map(NAME_POPULARITY_EXTRACTOR)
                .filter(entry -> validateString(entry.getKey(), restrictedValues))
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
                .map(node -> Map.entry(getText(node, NAME), getLong(node.get("followers"), TOTAL)))
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
        final Integer year = parseInt(getText(node, "release_date").split("-")[0]);
        final String releaseNameAndType = format("%s : %s", getText(node, NAME), getText(node, "album_type"));

        if (result.containsKey(year)) {
            if (!requireNonNull(result.get(year)).contains(releaseNameAndType))
                requireNonNull(result.get(year)).add(releaseNameAndType);
        } else {
            List<String> list = new ArrayList<>();
            list.add(releaseNameAndType);
            result.put(year, list);
        }
    }

    private String getText(JsonNode node, String name) {
        try {
            return node.get(name).asText();
        } catch (Exception e) {
            throw new RuntimeException("Parsing error", e);
        }
    }

    private int getInt(JsonNode node, String name) {
        try {
            return node.get(name).asInt();
        } catch (Exception e) {
            throw new RuntimeException("Parsing error", e);
        }
    }

    private long getLong(JsonNode node, String name) {
        try {
            return node.get(name).asLong();
        } catch (Exception e) {
            throw new RuntimeException("Parsing error", e);
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
