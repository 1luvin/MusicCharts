package com.vance.lib.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.parser.MusicbrainzParser;
import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.url.UrlBuilder;
import org.jetbrains.annotations.TestOnly;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MusicbrainzIntegration {
    public static final int YEARS_90_TO_99 = 0;
    public static final int YEARS_00_TO_09 = 1;
    public static final int YEARS_10_TO_19 = 2;
    private final RequestService requestService;
    private final UrlBuilder urlBuilder = new UrlBuilder();
    private final MusicbrainzParser parser = new MusicbrainzParser();
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final List<String> century1990 = List.of("1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999");
    private final List<String> century2000 = List.of("2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009");
    private final List<String> century2010 = List.of("2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019");
    private int waitTime = 500;

    public MusicbrainzIntegration(RequestService requestService) {
        this.requestService = requestService;
    }

    public Map<String, Long> getNumberOfArtistsOfGenres(List<String> genres) {
        final Map<String, Long> result = new LinkedHashMap<>();
        genres.forEach(genre -> addCountToMap(result, genre, urlBuilder.musicbrainz().numberOfArtistsOfGenre(genre).build()));
        return result;
    }

    public Map<String, Long> getNumberOfReleasesOfGenres(List<String> genres) {
        final Map<String, Long> result = new LinkedHashMap<>();
        genres.forEach(genre -> addCountToMap(result, genre, urlBuilder.musicbrainz().releasesOfGenre(genre).build()));
        return result;
    }

    public Map<String, Long> getNumberOfReleasesOfGenreInYears(int years, String genre) {
        validateYears(years);

        final Map<String, Long> result = new LinkedHashMap<>();
        if (years == YEARS_90_TO_99)
            setNumberOfReleases(century1990, result, genre);
        else if (years == YEARS_00_TO_09)
            setNumberOfReleases(century2000, result, genre);
        else if (years == YEARS_10_TO_19)
            setNumberOfReleases(century2010, result, genre);
        return result;
    }

    @TestOnly
    public void setWaitTime(int value) {
        this.waitTime = value;
    }

    private void setNumberOfReleases(List<String> years, Map<String, Long> map, String genre) {
        years.forEach(year -> addCountToMap(map, year, urlBuilder.musicbrainz().releasesOfGenre(genre, year).build()));
    }

    public String getYearsFormatted(int years) {
        validateYears(years);
        if (years == YEARS_00_TO_09)
            return "2000 - 2009";
        if (years == YEARS_90_TO_99)
            return "1990 - 1999";
        else
            return "2010 - 2019";
    }

    private void validateYears(int years) {
        if (years != YEARS_00_TO_09 && years != YEARS_90_TO_99 && years != YEARS_10_TO_19) {
            throw new MusicbrainzIntegrationException("Unsupported years");
        }
    }

    private void addCountToMap(Map<String, Long> map, String item, String url) {
        final String finalUrl = String.format("%s&fmt=json", url);

        try {
            Thread.sleep(waitTime); // we need this for not to be banned :) | ! Can be reduced for a faster loading !
            final String response = requestService.sendRequest(requestBuilder.get().url(finalUrl).build());
            map.put(item, parser.parseCountOfItems(response));

        } catch (JsonProcessingException | InterruptedException e) {
            throw new MusicbrainzIntegrationException("Error occurred: " + e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format("Bad url created: %s", finalUrl));
        }
    }

    static class MusicbrainzIntegrationException extends RuntimeException {
        public MusicbrainzIntegrationException(String message) {
            super(message);
        }

        public MusicbrainzIntegrationException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
