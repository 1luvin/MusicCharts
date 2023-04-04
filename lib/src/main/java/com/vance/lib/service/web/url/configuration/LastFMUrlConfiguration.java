package com.vance.lib.service.web.url.configuration;

public class LastFMUrlConfiguration extends UrlConfiguration {

    public LastFMUrlConfiguration() {
        resultUrl.append("http://ws.audioscrobbler.com/2.0");
    }
}
