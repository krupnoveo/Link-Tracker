package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.clients.GitHubClient;
import edu.java.dto.LinkData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class GitHubClientTest {
    private static WireMockServer server;

    @BeforeAll
    public static void startServer() {
        server = new WireMockServer();
        server
            .stubFor(get(urlPathEqualTo("/repos/krupnoveo/Link-Tracker"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""
                            {
                                "id": 751923883,
                                "full_name":"krupnoveo/Link-Tracker",
                                "created_at":"2024-02-02T16:16:12Z",
                                "updated_at":"2024-02-02T16:49:20Z",
                                "pushed_at":"2024-02-12T20:16:50Z"
                            }
                            """
                    )
                )
            );
        server.stubFor(get(urlPathEqualTo("/repos/admin/repositoriy"))
            .willReturn(aResponse()
                .withStatus(404)
                ));
        server.start();
    }

    @Test
    @SneakyThrows
    public void isSupported_shouldReturnTrue_whenLinkIsValid() {
        GitHubClient gitHubClient = new GitHubClient();
        URL url = new URI("https://github.com/krupnoveo/Link-Tracker").toURL();

        assertThat(gitHubClient.isUrlSupported(url)).isTrue();
    }

    @Test
    @SneakyThrows
    public void isSupported_shouldReturnFalse_whenLinkIsInvalid() {
        GitHubClient gitHubClient = new GitHubClient();
        URL url1 = new URI("https://github.com/krupnoveo").toURL();
        URL url2 = new URI("https://gitlab.com/krupnoveo/Link-Tracker").toURL();
        URL url3 = new URI("http://gitlab.com/krupnoveo/Link-Tracker").toURL();

        assertThat(gitHubClient.isUrlSupported(url1)).isFalse();
        assertThat(gitHubClient.isUrlSupported(url2)).isFalse();
        assertThat(gitHubClient.isUrlSupported(url3)).isFalse();
    }


    @Test
    @SneakyThrows
    public void checkURL_shouldReturnCorrectAnswer_whenServerResponse200() {
        GitHubClient gitHubClient = new GitHubClient(server.baseUrl());
        URL url = new URI("https://github.com/krupnoveo/Link-Tracker").toURL();
        LinkData data = gitHubClient.checkURL(url);
        OffsetDateTime date = OffsetDateTime.parse("2024-02-02T16:49:20Z");

        assertThat(url).isEqualTo(data.url());
        assertThat(date).isEqualTo(data.lastUpdated());
    }

    @Test
    @SneakyThrows
    public void checkURL_shouldReturnCorrectAnswer_whenServerResponse404() {
        GitHubClient gitHubClient = new GitHubClient(server.baseUrl());
        URL url = new URI("https://github.com/admin/repositoriy").toURL();
        LinkData data = gitHubClient.checkURL(url);

        assertThat(data.url()).isNull();
        assertThat(data.lastUpdated()).isNull();
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }
}
