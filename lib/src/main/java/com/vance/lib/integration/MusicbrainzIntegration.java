package com.vance.lib.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vance.lib.service.parser.MusicbrainzParser;
import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.url.UrlBuilder;
import org.jetbrains.annotations.TestOnly;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.vance.lib.util.LambdaExceptionUtil.rethrowConsumer;

public class MusicbrainzIntegration {
    public static final int YEARS_90_TO_99 = 0;
    public static final int YEARS_00_TO_09 = 1;
    public static final int YEARS_10_TO_19 = 2;
    private final RequestService requestService;
    private final UrlBuilder urlBuilder = new UrlBuilder();
    private final MusicbrainzParser parser = new MusicbrainzParser();
    private final HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private final List<String> decade1990 = List.of("1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999");
    private final List<String> decade2000 = List.of("2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009");
    private final List<String> decade2010 = List.of("2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019");
    private final List<String> genres = List.of("Pop", "Rock", "Jazz", "Blues", "Classical", "Rap", "Electronic");
    private final List<String> genresWithoutOne = List.of("Pop", "Rock", "Jazz", "Blues", "Classical", "Rap");
    private int waitTime = 600;

    public MusicbrainzIntegration(RequestService requestService) {
        this.requestService = requestService;
    }

    public Map<String, Long> getNumberOfArtistsOfGenres(String genre) throws IntegrationException {
        final Map<String, Long> result = new LinkedHashMap<>();
        getGenres(genre).forEach(rethrowConsumer(value ->
                addCountToMap(result, value, urlBuilder.musicbrainz().numberOfArtistsOfGenre(value).build())));
        return result;
    }

    public Map<String, Long> getNumberOfReleasesOfGenres(String genre) throws IntegrationException {
        final Map<String, Long> result = new LinkedHashMap<>();
        getGenres(genre).forEach(rethrowConsumer(value ->
                addCountToMap(result, value, urlBuilder.musicbrainz().releasesOfGenre(value).build())));
        return result;
    }

    public Map<String, Long> getNumberOfReleasesOfGenreInYears(int years, String genre) throws IntegrationException {
        validateYears(years);

        final Map<String, Long> result = new LinkedHashMap<>();

        if (years == YEARS_90_TO_99)
            setNumberOfReleases(decade1990, result, genre);
        else if (years == YEARS_00_TO_09)
            setNumberOfReleases(decade2000, result, genre);
        else if (years == YEARS_10_TO_19)
            setNumberOfReleases(decade2010, result, genre);

        return result;
    }

    @TestOnly
    public void setWaitTime(int millis) {
        this.waitTime = millis;
    }

    public String getYearsFormatted(int years) throws MusicbrainzIntegrationException {
        validateYears(years);
        if (years == YEARS_00_TO_09)
            return "2000 - 2009";
        if (years == YEARS_90_TO_99)
            return "1990 - 1999";
        else
            return "2010 - 2019";
    }

    private void validateYears(int years) throws MusicbrainzIntegrationException {
        if (years != YEARS_00_TO_09 && years != YEARS_90_TO_99 && years != YEARS_10_TO_19) {
            throw new MusicbrainzIntegrationException("Unsupported years");
        }
    }

    private void setNumberOfReleases(List<String> years, Map<String, Long> map, String genre) throws IntegrationException {
        years.forEach(rethrowConsumer(year ->
                addCountToMap(map, year, urlBuilder.musicbrainz().releasesOfGenre(genre, year).build())));
    }

    private void addCountToMap(Map<String, Long> map, String item, String url) throws MusicbrainzIntegrationException {
        final String finalUrl = String.format("%s&fmt=json", url);

        try {
            Thread.sleep(waitTime); // we need this for not to be banned :) | ! Can be reduced for a faster loading !
            final String response = requestService.sendRequest(requestBuilder.get().url(finalUrl).build());
            map.put(item, parser.parseCountOfItems(response));

        } catch (JsonProcessingException | InterruptedException e) {
            throw new MusicbrainzIntegrationException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new MusicbrainzIntegrationException(String.format("Bad url created: %s", finalUrl), e);
        }
    }

    private List<String> getGenres(String genre) {
        return genres.contains(genre) ? genres : createUniqueGenreList(genre);
    }

    private List<String> createUniqueGenreList(String genre) {
        final List<String> result = new ArrayList<>(genresWithoutOne);
        result.add(genre);
        return result;
    }

    static class MusicbrainzIntegrationException extends IntegrationException {
        public MusicbrainzIntegrationException(String message, Exception throwable) {
            super(message, throwable);
        }

        public MusicbrainzIntegrationException(String message) {
            super(message);
        }
    }
}
