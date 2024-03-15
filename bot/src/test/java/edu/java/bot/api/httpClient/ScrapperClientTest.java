package edu.java.bot.api.httpClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.bot.api.dto.request.AddLinkRequest;
import edu.java.bot.api.dto.request.RemoveLinkRequest;
import edu.java.bot.api.dto.response.ApiErrorResponse;
import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.Link;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.models.Chat;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class ScrapperClientTest {
    private final static String PATH_FOR_CHAT_CONTROLLER = "/tg-chat/1";
    private final static String PATH_FOR_LINKS_CONTROLLER = "/links";



    @Test
    @DisplayName("Тест ScrapperClient.registerChat(), скраппер возвращает статус 200")
    public void registerChat_whenReturned200_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(post(urlPathEqualTo(PATH_FOR_CHAT_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody((String) null)
            )
        );
        server.start();
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<Void> actual = client.registerChat(new Chat(1L));

        assertThat(actual.errorResponse()).isNull();
        assertThat(actual.response()).isNull();
        server.stop();
    }

    @Test
    @DisplayName("Тест ScrapperClient.registerChat(), скраппер возвращает статус 409")
    public void registerChat_whenReturned409_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(post(urlPathEqualTo(PATH_FOR_CHAT_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(409)
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
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<Void> actual = client.registerChat(new Chat(1L));
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

    @Test
    @DisplayName("Тест ScrapperClient.deleteChat(), скраппер возвращает статус 200")
    public void deleteChat_whenReturned200_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(delete(urlPathEqualTo(PATH_FOR_CHAT_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody((String) null)
            )
        );
        server.start();
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<Void> actual = client.deleteChat(new Chat(1L));

        assertThat(actual.errorResponse()).isNull();
        assertThat(actual.response()).isNull();
        server.stop();
    }

    @Test
    @DisplayName("Тест ScrapperClient.deleteChat(), скраппер возвращает статус 404")
    public void deleteChat_whenReturned404_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(delete(urlPathEqualTo(PATH_FOR_CHAT_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(404)
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
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<Void> actual = client.deleteChat(new Chat(1L));
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

    @Test
    @SneakyThrows
    @DisplayName("Тест ScrapperClient.listLinks(), скраппер возвращает статус 200")
    public void listLinks_whenReturned200_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(get(urlPathEqualTo(PATH_FOR_LINKS_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "links": [
                        {
                          "id": 0,
                          "url": "string"
                        }
                      ]
                    }
                    """)
            )
        );
        server.start();
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<ListLinksResponse> actual = client.listLinks(1L);
        GenericResponse<ListLinksResponse> expected = new GenericResponse<>(
            new ListLinksResponse(List.of(new Link(0L, new URI("string")))), null
        );
        assertThat(actual.errorResponse()).isNull();
        assertThat(actual.response()).isEqualTo(expected.response());
        server.stop();
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ScrapperClient.listLinks(), скраппер возвращает статус 404")
    public void listLinks_whenReturned404_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(get(urlPathEqualTo(PATH_FOR_LINKS_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(404)
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
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<ListLinksResponse> actual = client.listLinks(1L);
        GenericResponse<ListLinksResponse> expected = new GenericResponse<>(
            null, new ApiErrorResponse(
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

    @Test
    @SneakyThrows
    @DisplayName("Тест ScrapperClient.addLinkToTracking(), скраппер возвращает статус 200")
    public void addLinkToTracking_whenReturned200_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(post(urlPathEqualTo(PATH_FOR_LINKS_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                       "id": 0,
                       "url": "string"
                     }
                    """)
            )
        );
        server.start();
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<AddLinkToDatabaseResponse> actual = client.addLinkToTracking(1L, new AddLinkRequest(new URI("string")));
        GenericResponse<AddLinkToDatabaseResponse> expected = new GenericResponse<>(
            new AddLinkToDatabaseResponse(0L, new URI("string")), null
        );

        assertThat(actual.errorResponse()).isNull();
        assertThat(actual.response()).isEqualTo(expected.response());
        server.stop();
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ScrapperClient.addLinkToTracking(), скраппер возвращает статус 406")
    public void addLinkToTracking_whenReturned406_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(post(urlPathEqualTo(PATH_FOR_LINKS_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(406)
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
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<AddLinkToDatabaseResponse> actual = client.addLinkToTracking(1L, new AddLinkRequest(new URI("string")));
        GenericResponse<AddLinkToDatabaseResponse> expected = new GenericResponse<>(
            null, new ApiErrorResponse(
            "string",
            "string",
            "string",
            "string",
            List.of("string")
            )
        );

        assertThat(actual.response()).isNull();
        assertThat(actual.errorResponse()).isEqualTo(expected.errorResponse());
        server.stop();
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ScrapperClient.removeLinkFromTracking(), скраппер возвращает статус 200")
    public void removeLinkFromTracking_whenReturned200_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(delete(urlPathEqualTo(PATH_FOR_LINKS_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                       "id": 0,
                       "url": "string"
                     }
                    """)
            )
        );
        server.start();
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<RemoveLinkFromDatabaseResponse> actual = client.removeLinkFromTracking(1L, new RemoveLinkRequest(0L));
        GenericResponse<RemoveLinkFromDatabaseResponse> expected = new GenericResponse<>(new RemoveLinkFromDatabaseResponse(0L, new URI("string")), null);

        assertThat(actual.errorResponse()).isNull();
        assertThat(actual.response()).isEqualTo(expected.response());
        server.stop();
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ScrapperClient.removeLinkFromTracking(), скраппер возвращает статус 404")
    public void removeLinkFromTracking_whenReturned404_shouldReturnCorrectResponse() {
        WireMockServer server = new WireMockServer();
        server.stubFor(delete(urlPathEqualTo(PATH_FOR_LINKS_CONTROLLER))
            .willReturn(aResponse()
                .withStatus(404)
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
        ScrapperClient client = new ScrapperClient(server.baseUrl());
        GenericResponse<RemoveLinkFromDatabaseResponse> actual = client.removeLinkFromTracking(1L, new RemoveLinkRequest(0L));
        GenericResponse<RemoveLinkFromDatabaseResponse> expected = new GenericResponse<>(
            null, new ApiErrorResponse(
            "string",
            "string",
            "string",
            "string",
            List.of("string")
            )
        );

        assertThat(actual.response()).isNull();
        assertThat(actual.errorResponse()).isEqualTo(expected.errorResponse());
        server.stop();
    }
}
