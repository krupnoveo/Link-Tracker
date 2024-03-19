package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.clients.GitHubClient;
import edu.java.configuration.ApplicationConfig;
import edu.java.models.LinkData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class GitHubClientTest {
    private static WireMockServer server;

    @BeforeAll
    public static void startServer() {
        server = new WireMockServer();
        server
            .stubFor(get(urlPathEqualTo("/repos/krupnoveo/Link-Tracker/events"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""
                            [
                                {
                                    "type": "PushEvent",
                                    "payload": {
                                        "size": 1,
                                        "ref": "refs/heads/main"
                                    },
                                    "created_at": "2024-03-17T18:15:35Z"
                                }
                            ]
                            """
                    )
                )
            );
        server.stubFor(get(urlPathEqualTo("/repos/admin/repositoriy/events"))
            .willReturn(aResponse()
                .withStatus(404)
                ));
        server.start();
    }

    @Test
    @SneakyThrows
    public void isSupported_shouldReturnTrue_whenLinkIsValid() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        GitHubClient gitHubClient = new GitHubClient(server.baseUrl(), properties);
        URL url = new URI("https://github.com/krupnoveo/Link-Tracker").toURL();

        assertThat(gitHubClient.isUrlSupported(url)).isTrue();
    }

    @Test
    @SneakyThrows
    public void isSupported_shouldReturnFalse_whenLinkIsInvalid() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        GitHubClient gitHubClient = new GitHubClient(server.baseUrl(), "", properties);
        URL url1 = new URI("https://github.com/krupnoveo").toURL();
        URL url2 = new URI("https://gitlab.com/krupnoveo/Link-Tracker").toURL();
        URL url3 = new URI("http://gitlab.com/krupnoveo/Link-Tracker").toURL();

        assertThat(gitHubClient.isUrlSupported(url1)).isFalse();
        assertThat(gitHubClient.isUrlSupported(url2)).isFalse();
        assertThat(gitHubClient.isUrlSupported(url3)).isFalse();
    }


    @Test
    @SneakyThrows
    public void checkURL_shouldReturnCorrectAnswer_whenServerResponse200_and_lastUpdatedIsNotNull() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        GitHubClient gitHubClient = new GitHubClient(server.baseUrl(), properties);
        URL url = new URI("https://github.com/krupnoveo/Link-Tracker").toURL();
        List<LinkData> data = gitHubClient.checkURL(url, OffsetDateTime.MIN);
        OffsetDateTime date = OffsetDateTime.parse("2024-03-17T18:15:35Z");

        assertThat(url).isEqualTo(data.get(0).url());
        assertThat(date).isEqualTo(data.get(0).lastUpdated());
    }

    @Test
    @SneakyThrows
    public void checkURL_shouldReturnCorrectAnswer_whenServerResponse200_and_lastUpdatedIsNull() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        GitHubClient gitHubClient = new GitHubClient(server.baseUrl(), properties);
        URL url = new URI("https://github.com/krupnoveo/Link-Tracker").toURL();
        List<LinkData> data = gitHubClient.checkURL(url, null);
        OffsetDateTime date = OffsetDateTime.parse("2024-03-17T18:15:35Z");

        assertThat(url).isEqualTo(data.get(0).url());
        assertThat(date).isEqualTo(data.get(0).lastUpdated());
    }

    @Test
    @SneakyThrows
    public void checkURL_shouldReturnCorrectAnswer_whenServerResponse404() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        GitHubClient gitHubClient = new GitHubClient(server.baseUrl(), properties);
        URL url = new URI("https://github.com/admin/repositoriy").toURL();
        List<LinkData> data = gitHubClient.checkURL(url, OffsetDateTime.MIN);

        assertThat(data.get(0).url()).isNull();
        assertThat(data.get(0).lastUpdated()).isNull();
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }
}
