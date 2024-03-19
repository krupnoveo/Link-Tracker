package edu.java.scrapper.clients.dataHandling;

import edu.java.clients.dataHandling.GitHubClientDataHandler;
import edu.java.models.GitHubEvents;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

public class GitHubClientDataHandlerTest {
    @Test
    public void getMessageByDescriptionAndHost_whenDescriptionIsNull() {
        Properties properties = Mockito.mock(Properties.class);
        GitHubEvents.EventsHolder eventsHolder = Mockito.mock(GitHubEvents.EventsHolder.class);
        GitHubClientDataHandler handler = new GitHubClientDataHandler(properties, eventsHolder);

        assertThat(handler.getMessageByDescription(null)).isNull();
    }

    @Test
    @SneakyThrows
    public void getMessageByDescriptionAndHost_whenDescriptionIsNotNull() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        GitHubEvents.EventsHolder eventsHolder = Mockito.mock(GitHubEvents.EventsHolder.class);
        GitHubClientDataHandler handler = new GitHubClientDataHandler(properties, eventsHolder);
        Map<String, String> description = Map.of(
            "type", "PushEvent",
            "ref", "refs/heads/master",
            "size", "1"
        );
        Mockito.when(eventsHolder.getEvents()).thenReturn(List.of(
            new GitHubEvents.PushEvent(properties),
            new GitHubEvents.IssuesEvent(properties)
        ));
        String actual = handler.getMessageByDescription(description);
        String expected = "По ссылке %s в ветку master были добавлены коммиты в количестве: 1 штук(-а)";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    public void isHostSupported_shouldReturnCorrectResult() {
        String host1 = "github.com";
        String host2 = "gitlab.com";
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        GitHubEvents.EventsHolder eventsHolder = Mockito.mock(GitHubEvents.EventsHolder.class);
        GitHubClientDataHandler handler = new GitHubClientDataHandler(properties, eventsHolder);
        assertThat(handler.isHostSupported(host1)).isTrue();
        assertThat(handler.isHostSupported(host2)).isFalse();
    }
}
