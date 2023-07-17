package com.vance.lib.integration;

import com.vance.lib.service.parser.LastFmParser;
import com.vance.lib.service.parser.ParsingException;
import com.vance.lib.service.web.http.RequestService;
import com.vance.lib.service.web.secrets.SecretProvider;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static com.vance.lib.util.FileUtil.readFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LastFmIntegrationTest {
    private final String TEST_TOKEN = "TEST_TOKEN";
    private final LastFmParser parser = new LastFmParser();
    private final RequestService requestService = mock(RequestService.class);
    private final SecretProvider secretProvider = mock(SecretProvider.class);

    private final LastFmIntegration lastFmIntegration = new LastFmIntegration(requestService, secretProvider);

    @Test
    void shouldGetPopularityOfGenres() throws IOException, ParsingException, IntegrationException {
        // given
        final String testResponse = readFile("lastfm/PopularityOfGenres.json", LastFmIntegrationTest.class);
        final Map<String, Long> expected = parser.parsePopularityOfGenres(testResponse);

        given(secretProvider.getLastFMToken()).willReturn(TEST_TOKEN);
        given(requestService.sendRequest(any(ClassicHttpRequest.class))).willReturn(testResponse);

        // when
        final Map<String, Long> actual = lastFmIntegration.getPopularityOfGenres();

        // then
        verify(requestService, times(1)).sendRequest(any(ClassicHttpRequest.class));
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowLastFmIntegrationException() {
        // given
        final String testResponse = "---";
        given(secretProvider.getLastFMToken()).willReturn(TEST_TOKEN);
        given(requestService.sendRequest(any())).willReturn(testResponse);

        // when & then
        assertThrows(LastFmIntegration.LastFmIntegrationException.class, lastFmIntegration::getPopularityOfGenres);
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenProvidingBadUrl() {
        // given
        final String badToken = "/,.1231.....}[";
        given(secretProvider.getLastFMToken()).willReturn(badToken);

        // when & then
        assertThrows(LastFmIntegration.LastFmIntegrationException.class, lastFmIntegration::getPopularityOfGenres);
    }
}