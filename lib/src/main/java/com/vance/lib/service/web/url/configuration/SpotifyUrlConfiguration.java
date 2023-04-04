package com.vance.lib.service.web.url.configuration;

import org.jetbrains.annotations.NotNull;

public class SpotifyUrlConfiguration extends UrlConfiguration {


    public SpotifyUrlConfiguration() {
        resultUrl.append("https://api.spotify.com/v1");
    }

    public SpotifyUrlConfiguration search(@NotNull String query, @NotNull SpotifySearchTypes type) {
        resultUrl.append(String.format("/search?q=%s&type=%s", query, type.name().toLowerCase()));
        return this;
    }

    public SpotifyUrlConfiguration artist(@NotNull String artistID) {
        resultUrl.append(String.format("/artists/%s", artistID));
        return this;
    }

    public SpotifyUrlConfiguration albumsOfArtist(@NotNull String artistsID) {
        resultUrl.append(String.format("/artists/%s/albums", artistsID));
        return this;
    }

    public SpotifyUrlConfiguration album(@NotNull String albumID) {
        resultUrl.append(String.format("/albums/%s", albumID));
        return this;
    }

    public SpotifyUrlConfiguration albumTracks(@NotNull String albumID) {
        resultUrl.append(String.format("/album/%s/tracks", albumID));
        return this;
    }

    //todo: Add url that we need
}
