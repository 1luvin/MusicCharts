package com.vance.lib.service.web.url;

import com.vance.lib.service.web.url.configuration.DiscogsUrlConfiguration;
import com.vance.lib.service.web.url.configuration.LastFMUrlConfiguration;
import com.vance.lib.service.web.url.configuration.MusicbrainzUrlConfiguration;
import com.vance.lib.service.web.url.configuration.SpotifyUrlConfiguration;

public class UrlBuilder {

    public DiscogsUrlConfiguration discogs() {
        return new DiscogsUrlConfiguration();
    }

    public SpotifyUrlConfiguration spotify() {
        return new SpotifyUrlConfiguration();
    }

    public LastFMUrlConfiguration lastfm() {
        return new LastFMUrlConfiguration();
    }

    public MusicbrainzUrlConfiguration musicbrainz() {
        return new MusicbrainzUrlConfiguration();
    }

    public String spotifyToken() {
        return "https://accounts.spotify.com/api/token";
    }
}
