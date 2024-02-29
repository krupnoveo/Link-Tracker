package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.clients.StackOverflowClient;
import edu.java.dto.LinkData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowClientTest {
    private static WireMockServer server;

    @BeforeAll
    public static void startServer() {
        server = new WireMockServer();
        server
            .stubFor(get(urlPathEqualTo("/questions/123/"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""
                            {
                                "items":
                                    [
                                    {"user_id":120682,
                                        "display_name":"jyoungdev",
                                        "last_activity_date":1333087111,
                                        "creation_date":1331093920,
                                        "last_edit_date":1333087111,
                                        "question_id":9595781
                                        }
                                    ]
                            }
                            """
                    )
                )
            );
        server.stubFor(get(urlPathEqualTo("/questions/0/find"))
            .willReturn(aResponse()
                .withStatus(404)
            ));
        server.start();
    }

    @Test
    @SneakyThrows
    public void isSupported_shouldReturnTrue_whenLinkIsValid() {
        StackOverflowClient stackOverflowClient = new StackOverflowClient();
        URL url = new URI("https://stackoverflow.com/questions/123/help").toURL();

        assertThat(stackOverflowClient.isUrlSupported(url)).isTrue();
    }

    @Test
    @SneakyThrows
    public void isSupported_shouldReturnFalse_whenLinkIsInvalid() {
        StackOverflowClient stackOverflowClient = new StackOverflowClient();
        URL url1 = new URI("https://stackoverflow.com/123").toURL();
        URL url2 = new URI("https://github.com/123/help").toURL();
        URL url3 = new URI("http://stackoverflow/123/help").toURL();

        assertThat(stackOverflowClient.isUrlSupported(url1)).isFalse();
        assertThat(stackOverflowClient.isUrlSupported(url2)).isFalse();
        assertThat(stackOverflowClient.isUrlSupported(url3)).isFalse();
    }


    @Test
    @SneakyThrows
    public void checkURL_shouldReturnCorrectAnswer_whenServerResponse200() {
        StackOverflowClient stackOverflowClient = new StackOverflowClient(server.baseUrl());
        URL url = new URI("https://stackoverflow.com/questions/123/help").toURL();
        LinkData data = stackOverflowClient.checkURL(url);
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1333087111), ZoneId.systemDefault());

        assertThat(url).isEqualTo(data.url());
        assertThat(date).isEqualTo(data.lastUpdated());
    }

    @Test
    @SneakyThrows
    public void checkURL_shouldReturnCorrectAnswer_whenServerResponse404() {
        StackOverflowClient stackOverflowClient = new StackOverflowClient(server.baseUrl());
        URL url = new URI("https://stackoverflow.com/0/find").toURL();
        LinkData data = stackOverflowClient.checkURL(url);

        assertThat(data.url()).isNull();
        assertThat(data.lastUpdated()).isNull();
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }
}
