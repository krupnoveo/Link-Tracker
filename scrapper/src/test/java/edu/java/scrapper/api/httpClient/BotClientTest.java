package edu.java.scrapper.api.httpClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.api.dto.response.ApiErrorResponse;
import edu.java.api.httpClient.BotClient;
import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class BotClientTest {
    private final static String PATH_FOR_LINKS_UPDATE_CONTROLLER = "/updates";

    @Test
    @SneakyThrows
    @DisplayName("Тест BotClient.updateLinks(), бот возвращает статус 200")
    public void registerChat_whenReturned200_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(post(urlPathEqualTo(PATH_FOR_LINKS_UPDATE_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody((String) null)
            )
        );
        server.start();
        BotClient client = new BotClient(server.baseUrl());
        GenericResponse<Void> actual = client.updateLinks(new LinkUpdate(
            1L,
            new URI(""),
            "",
            List.of()
        ));

        assertThat(actual.errorResponse()).isNull();
        assertThat(actual.response()).isNull();
        server.stop();
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест BotClient.updateLinks(), бот возвращает статус 400")
    public void registerChat_whenReturned409_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(post(urlPathEqualTo(PATH_FOR_LINKS_UPDATE_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                       "description": "string",
                       "code": "string",
                       "exceptionName": "string",
                       "exceptionMessage": "string",
                       "stacktrace": [
                         "string"
                       ]
                     }
                    """)
            )
        );
        server.start();
        BotClient client = new BotClient(server.baseUrl());
        GenericResponse<Void> actual = client.updateLinks(new LinkUpdate(
            1L,
            new URI(""),
            "",
            List.of()
        ));
        GenericResponse<Void> expected = new GenericResponse<>(null, new ApiErrorResponse(
            "string",
            "string",
            "string",
            "string",
            List.of("string")
        ));
        assertThat(actual.response()).isNull();
        assertThat(actual.errorResponse()).isEqualTo(expected.errorResponse());
        server.stop();
    }
}
