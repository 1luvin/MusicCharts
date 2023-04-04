package com.vance.lib;

import com.vance.lib.service.web.http.HttpRequestBuilder;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.secrets.SecretProvider;
import com.vance.lib.service.web.url.UrlBuilder;
import com.vance.lib.service.web.url.configuration.SpotifySearchTypes;

import java.net.URISyntaxException;

public class DataProviderAPI {


    //todo: Currently is used for tests!!!
    public static void main(String[] args) {
        RequestService requestService = RequestService.getInstance();
        SecretProvider secretProvider = SecretProvider.getInstance(requestService);
        final String authToken = secretProvider.getSpotifyToken();
        try {
            String result = requestService.sendRequest(new HttpRequestBuilder().
                    get()
                    .url(new UrlBuilder().spotify()
                            .search("Nirvana", SpotifySearchTypes.ARTIST)
                            .build())
                    .addHeader("Authorization", "Bearer  " + authToken)
                    .build());
            System.out.println(result);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}