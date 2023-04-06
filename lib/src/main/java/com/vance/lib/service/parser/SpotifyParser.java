package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpotifyParser {

    private final Logger log = LoggerFactory.getLogger(SpotifyParser.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String parseIdOfItem(@NotNull String item, SpotifySearchTypes type) throws JsonProcessingException {
        return objectMapper.readTree(item).get(type.name().toLowerCase() + "s")
                .get("items")
                .get(0)
                .get("id").asText();
    }

    public String parseIdOfItems(@NotNull String items, SpotifySearchTypes type) throws JsonProcessingException {
        final JsonNode parsedItems = objectMapper.readTree(items).get(type.name().toLowerCase() + "s").get("items");
        if (!parsedItems.isArray())
            throw new RuntimeException("Cannot parse ids of " + type);

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(parsedItems.iterator(), Spliterator.ORDERED), true)
                .map(node -> node.get("id").asText())
                .collect(Collectors.joining(","));
    }

    public List<Pair<String, Long>> parseDurationOfTracksFromAlbum(@NotNull String albumTracks) throws JsonProcessingException {
        final JsonNode tracks = objectMapper.readTree(albumTracks).get("items");
        if (!tracks.isArray())
            throw new RuntimeException("Cannot parse tracks of album as it is not an array of nodes");

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(tracks.iterator(), Spliterator.ORDERED), true)
                .map(node ->
                        Pair.of(node.get("name").asText(), node.get("duration_ms").asLong()))
                .collect(Collectors.toList());
    }

    public List<Pair<String, Integer>> parsePopularityOfAlbums(@NotNull String albums) throws JsonProcessingException {
        final JsonNode parsedAlbums = objectMapper.readTree(albums).get("albums");
        if (!parsedAlbums.isArray())
            throw new RuntimeException("Cannot parse albums as it is not an array of nodes");

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(parsedAlbums.iterator(), Spliterator.ORDERED), true)
                .map(node ->
                        Pair.of(node.get("name").asText(), node.get("popularity").asInt()))
                .collect(Collectors.toList());
    }
}
