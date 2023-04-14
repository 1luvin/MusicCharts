package com.vance.lib.service.web.url.configuration;

import org.jetbrains.annotations.NotNull;

public class MusicbrainzUrlConfiguration extends UrlConfiguration {

    public MusicbrainzUrlConfiguration() {
        resultUrl.append("https://musicbrainz.org/ws/2");
    }

    public MusicbrainzUrlConfiguration releasesOfGenre(@NotNull String genre, @NotNull String date) {
        resultUrl.append("/release/").append("?query=date:").append(date).append("%20AND%20tag:").append(genre);
        return this;
    }

    public MusicbrainzUrlConfiguration releasesOfGenre(@NotNull String genre) {
        resultUrl.append("/release/").append(String.format("?query=tag:%s", genre));
        return this;
    }

    public MusicbrainzUrlConfiguration numberOfArtistsOfGenre(@NotNull String genre) {
        resultUrl.append("/artist/").append("?query=type:group%20AND%20tag:").append(genre);
        return this;
    }
}
