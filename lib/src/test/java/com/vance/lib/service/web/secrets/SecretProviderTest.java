package com.vance.lib.service.web.secrets;

import com.vance.lib.service.web.http.RequestService;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecretProviderTest {

    private static final RequestService requestService = mock(RequestService.class);
    private static final SecretProvider secretProvider = SecretProvider.getInstance(requestService);


    @Test
    void shouldReturnRightLastFMToken() {
        // given
        final String expectedToken = "00b3b9cd727acf09535e94ae2908fe63";

        // when
        final String actualToken = secretProvider.getLastFMToken();

        // then
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void shouldReturnRightSpotifyToken() {
        // given
        final String expectedToken = "SPOTIFY_TEST_TOKEN";
        final String response = "{ \"access_token\": \"SPOTIFY_TEST_TOKEN\" }";
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(response);

        // when
        final String actualToken = secretProvider.getSpotifyToken();

        // then
        verify(requestService, times(1)).sendRequest(any(ClassicHttpRequest.class));
        assertEquals(expectedToken, actualToken);
    }
}