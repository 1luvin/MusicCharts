package com.vance.lib;

import com.vance.lib.integration.SpotifyIntegration;

import java.util.Map;

public class ChartDataProvider {

    //todo: Currently is used for tests!!!
    public static void main(String[] args) {
        SpotifyIntegration spotifyIntegration = new SpotifyIntegration();

        final Map<String, Long> durationOfTracksInAlbum = spotifyIntegration.getDurationOfTracksInAlbum("White Pony");
        durationOfTracksInAlbum.forEach((entry, value) ->
                System.out.printf("Track name: %s,\t\t track duration: %s\n", entry, value));

        System.out.println();
        final Map<String, Integer> popularityOfAlbums = spotifyIntegration.getPopularityOfAlbums("Deftones");
        popularityOfAlbums.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("Album: %s,\t\t popularity: %s\n", entry.getKey(), entry.getValue()));

        System.out.println();
        final Map<String, Integer> tracksPopularityOfArtist = spotifyIntegration.getPopularityOfTracksOfArtist("Deftones");
        tracksPopularityOfArtist.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("Track: %s,\t\t\t\t popularity: %s\n", entry.getKey(), entry.getValue()));

        System.out.println();
        final Map<String, Integer> tracksPopularityOfTracksFromAlbum = spotifyIntegration.getPopularityOfTracksInAlbum("White Pony");
        tracksPopularityOfTracksFromAlbum.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("Track: %s,\t\t\t\t popularity: %s\n", entry.getKey(), entry.getValue()));
    }
}