package com.vance.lib.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.parser.MusicbrainzParser;
import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.url.UrlBuilder;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MusicbrainzIntegration {
    private final UrlBuilder urlBuilder = new UrlBuilder();
    private final MusicbrainzParser parser = new MusicbrainzParser();
    private final RequestService requestService = RequestService.getInstance();
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final List<String> ninetyNinthCentury = List.of("1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999");
    private final List<String> twentyCentury = List.of("2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009");
    private final List<String> twentyFirstCentury = List.of("2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019");
    public static final int YEARS_90_TO_99 = 0;
    public static final int YEARS_00_TO_09 = 1;
    public static final int YEARS_10_TO_19 = 2;


    public Map<String, Long> getNumberOfArtistsOfGenres(List<String> genres) {
        Map<String, Long> result = new LinkedHashMap<>();
        genres.forEach(genre ->
                addParsedCountToMap(result, genre, urlBuilder.musicbrainz().numberOfArtistsOfGenre(genre).build()));
        return result;

    }

    public Map<String, Long> getNumberOfReleasesOfGenres(List<String> genres) {
        Map<String, Long> result = new LinkedHashMap<>();
        genres.forEach(genre ->
                addParsedCountToMap(result, genre, urlBuilder.musicbrainz().releasesOfGenre(genre).build()));
        return result;
    }

    public Map<String, Long> getNumberOfReleasesOfGenreInYears(int years, String genre) {
        Map<String, Long> result = new LinkedHashMap<>();
        if (years == YEARS_90_TO_99) {
            ninetyNinthCentury.forEach(year ->
                    addParsedCountToMap(result, year, urlBuilder.musicbrainz().releasesOfGenre(genre, year).build()));
        } else if (years == YEARS_00_TO_09) {
            twentyCentury.forEach(year ->
                    addParsedCountToMap(result, year, urlBuilder.musicbrainz().releasesOfGenre(genre, year).build()));
        } else if (years == YEARS_10_TO_19) {
            twentyFirstCentury.forEach(year ->
                    addParsedCountToMap(result, year, urlBuilder.musicbrainz().releasesOfGenre(genre, year).build()));
        }
        return result;
    }

    private void addParsedCountToMap(Map<String, Long> map, String item, String url) {
        try {
            Thread.sleep(1500);
            final String response = sendRequestToMusicbrainz(url);
            map.put(item, parser.parseCountOfItems(response));
        } catch (JsonProcessingException | InterruptedException e) {
            throw new RuntimeException("Error occurred", e);
        }
    }

    private String sendRequestToMusicbrainz(@NotNull String url) {
        final String finalUrl = url + "&fmt=json";
        try {
            return requestService.sendRequest(requestBuilder.get().url(finalUrl).build());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Bad url given: %s", url));
        }
    }
}
