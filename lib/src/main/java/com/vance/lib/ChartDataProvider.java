package com.vance.lib;

import com.vance.lib.integration.SpotifyIntegration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ChartDataProvider {


    //todo: Currently is used for tests!!!
    public static void main(String[] args) {
        SpotifyIntegration spotifyIntegration = new SpotifyIntegration();

//        final List<Pair<String, Long>> popularityOfTracksInAlbum = spotifyIntegration.getDurationOfTracksInAlbum("White Pony");
//        popularityOfTracksInAlbum.forEach(pair ->
//                System.out.printf("Track name: %s, track duration: %s\n", pair.getKey(), pair.getValue()));
//        final List<Pair<String, Integer>> popularityOfAlbums = spotifyIntegration.getPopularityOfAlbums("Deftones");
//        popularityOfAlbums.forEach(pair ->
//                System.out.printf("Album: %s, popularity: %s\n", pair.getKey(), pair.getValue()));
    }
}