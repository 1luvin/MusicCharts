package com.vance.lib;

import com.vance.lib.integration.SpotifyIntegration;

import java.util.Map;

public class ChartDataProvider {

    //todo: Currently is used for tests!!!
    public static void main(String[] args) {
        SpotifyIntegration spotifyIntegration = new SpotifyIntegration();

        final Map<String, Long> durationOfTracksInAlbum = spotifyIntegration.getDurationOfTracksInAlbum("White Pony");
        durationOfTracksInAlbum.forEach((entry, value) ->
                System.out.printf("Track name: %s,\t\ttrack duration: %s\n", entry, value));
        final Map<String, Integer> popularityOfAlbums = spotifyIntegration.getPopularityOfAlbums("Deftones");
        System.out.println();
        popularityOfAlbums.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("Album: %s,\t\tpopularity: %s\n", entry.getKey(), entry.getValue()));
    }
}