package edu.java.scrapper.cliensHolder;

import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import edu.java.clientsHolder.ClientsHolder;
import edu.java.dto.LinkData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

public class ClientsHolderTest {
    @Test
    @SneakyThrows
    @DisplayName("Тест ClientsHolder.checkUrl(), когда приходит ссылка на GitHub")
    public void checkUrl_whenCheckedUrlIsFromGitHub_shouldReturnCorrectLinkData() {
        GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
        StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
        String stringUrl = "https://github.com";
        URL url = new URI(stringUrl).toURL();
        OffsetDateTime time = OffsetDateTime.now();
        Mockito.when(gitHubClient.isUrlSupported(url)).thenReturn(true);
        Mockito.when(gitHubClient.checkURL(url)).thenReturn(new LinkData(url, time));
        Mockito.when(stackOverflowClient.isUrlSupported(url)).thenReturn(false);

        ClientsHolder clientsHolder = new ClientsHolder(List.of(gitHubClient, stackOverflowClient));

        LinkData actual = clientsHolder.checkURl(stringUrl);
        LinkData expected = new LinkData(url, time);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ClientsHolder.checkUrl(), когда приходит ссылка на StackOverflow")
    public void checkUrl_whenCheckedUrlIsFromStackOverflow_shouldReturnCorrectLinkData() {
        GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
        StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
        String stringUrl = "https://stackoverflow.com";
        URL url = new URI(stringUrl).toURL();
        OffsetDateTime time = OffsetDateTime.now();
        Mockito.when(gitHubClient.isUrlSupported(url)).thenReturn(false);
        Mockito.when(stackOverflowClient.checkURL(url)).thenReturn(new LinkData(url, time));
        Mockito.when(stackOverflowClient.isUrlSupported(url)).thenReturn(true);

        ClientsHolder clientsHolder = new ClientsHolder(List.of(gitHubClient, stackOverflowClient));

        LinkData actual = clientsHolder.checkURl(stringUrl);
        LinkData expected = new LinkData(url, time);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ClientsHolder.checkUrl(), когда приходит ссылка не на GitHub и не на StackOverflow")
    public void checkUrl_whenCheckedUrlIsNotFromStackOverflowOrGitHub_shouldReturnCorrectLinkData() {
        GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
        StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
        String stringUrl = "https://stackoverflow.com";
        URL url = new URI(stringUrl).toURL();
        Mockito.when(gitHubClient.isUrlSupported(url)).thenReturn(false);
        Mockito.when(stackOverflowClient.isUrlSupported(url)).thenReturn(false);

        ClientsHolder clientsHolder = new ClientsHolder(List.of(gitHubClient, stackOverflowClient));

        LinkData actual = clientsHolder.checkURl(stringUrl);

        assertThat(actual).isNull();
    }
}
