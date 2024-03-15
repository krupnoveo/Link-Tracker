package edu.java.scrapper.cliensHolder;

import edu.java.api.exceptions.InvalidUrlFormatException;
import edu.java.api.exceptions.UnsupportedUrlException;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import edu.java.clientsHolder.ClientsHolder;
import edu.java.models.LinkData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClientsHolderTest {
    @Test
    @SneakyThrows
    @DisplayName("Тест ClientsHolder.checkUrl(), когда приходит ссылка на GitHub")
    public void checkUrl_whenCheckedUrlIsFromGitHub_shouldReturnCorrectLinkData() {
        GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
        StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
        URI uri = new URI("https://github.com");
        URL url = uri.toURL();
        OffsetDateTime time = OffsetDateTime.now();
        Mockito.when(gitHubClient.isUrlSupported(url)).thenReturn(true);
        Mockito.when(gitHubClient.checkURL(url)).thenReturn(new LinkData(url, time));
        Mockito.when(stackOverflowClient.isUrlSupported(url)).thenReturn(false);

        ClientsHolder clientsHolder = new ClientsHolder(List.of(gitHubClient, stackOverflowClient));

        LinkData actual = clientsHolder.checkURl(uri);
        LinkData expected = new LinkData(url, time);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ClientsHolder.checkUrl(), когда приходит ссылка на StackOverflow")
    public void checkUrl_whenCheckedUrlIsFromStackOverflow_shouldReturnCorrectLinkData() {
        GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
        StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
        URI uri = new URI("https://stackoverflow.com");
        URL url = uri.toURL();
        OffsetDateTime time = OffsetDateTime.now();
        Mockito.when(gitHubClient.isUrlSupported(url)).thenReturn(false);
        Mockito.when(stackOverflowClient.checkURL(url)).thenReturn(new LinkData(url, time));
        Mockito.when(stackOverflowClient.isUrlSupported(url)).thenReturn(true);

        ClientsHolder clientsHolder = new ClientsHolder(List.of(gitHubClient, stackOverflowClient));

        LinkData actual = clientsHolder.checkURl(uri);
        LinkData expected = new LinkData(url, time);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест ClientsHolder.checkUrl(), когда приходит ссылка не на GitHub и не на StackOverflow")
    public void checkUrl_whenCheckedUrlIsNotFromStackOverflowOrGitHub_shouldThrowUnsupportedUrlException() {
        GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
        StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
        URI uri = new URI("https://stackoverflow.com");
        URL url = uri.toURL();
        Mockito.when(gitHubClient.isUrlSupported(url)).thenReturn(false);
        Mockito.when(stackOverflowClient.isUrlSupported(url)).thenReturn(false);

        ClientsHolder clientsHolder = new ClientsHolder(List.of(gitHubClient, stackOverflowClient));

        UnsupportedUrlException exception = assertThrows(UnsupportedUrlException.class, () -> {
            clientsHolder.checkURl(uri);
        });
        assertThat(exception.getMessage()).isEqualTo(
            "Отслеживание ссылки %s на данный ресурс (часть ресурса) не поддерживается".formatted(uri)
        );
    }

    @Test
    @SneakyThrows
    public void checkUrl_whenCheckedUrlIsNotValid_shouldThrowInvalidUrlFormatException() {
        GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
        StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
        URI uri = new URI("htt://stackoverflow.com");

        ClientsHolder clientsHolder = new ClientsHolder(List.of(gitHubClient, stackOverflowClient));

        InvalidUrlFormatException exception = assertThrows(InvalidUrlFormatException.class, () -> {
            clientsHolder.checkURl(uri);
        });
        assertThat(exception.getMessage()).isEqualTo(
            "Ссылка %s имеет неправильный формат".formatted(uri)
        );
    }
}
