package com.vance.lib.service.web.url.configuration;

import org.jetbrains.annotations.NotNull;

public class SpotifyUrlConfiguration extends UrlConfiguration {


    public SpotifyUrlConfiguration() {
        resultUrl.append("https://api.spotify.com/v1");
    }

    public SpotifyUrlConfiguration search(@NotNull String query, @NotNull SpotifySearchTypes type, boolean limit) {
        resultUrl.append(String.format("/search?q=%s&type=%s", encodeQuery(query), type.name().toLowerCase()));
        if (limit)
            resultUrl.append("&limit=10");
        return this;
    }

    public SpotifyUrlConfiguration artist(@NotNull String artistID) {
        resultUrl.append(String.format("/artists/%s", artistID));
        return this;
    }

    public SpotifyUrlConfiguration albumsOfArtist(@NotNull String artistsID, boolean onlyAlbums) {
        resultUrl.append(String.format("/artists/%s/albums", artistsID));
        if (onlyAlbums)
            resultUrl.append("?include_groups=album&limit=50");
        else
            resultUrl.append("?include_groups=album,single,appears_on,compilation&limit=50");
        return this;
    }

    public SpotifyUrlConfiguration topTracksOfArtist(@NotNull String artistId) {
        resultUrl.append(String.format("/artists/%s/top-tracks?market=ES", artistId));
        return this;
    }

    public SpotifyUrlConfiguration album(@NotNull String albumID) {
        resultUrl.append(String.format("/albums/%s", albumID));
        return this;
    }

    public SpotifyUrlConfiguration albumTracks(@NotNull String albumID) {
        resultUrl.append(String.format("/albums/%s/tracks", albumID));
        return this;
    }

    public SpotifyUrlConfiguration track(@NotNull String trackID) {
        resultUrl.append(String.format("/tracks/%s", trackID));
        return this;
    }
}
