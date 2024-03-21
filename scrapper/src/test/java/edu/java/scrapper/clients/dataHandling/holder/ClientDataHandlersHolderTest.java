package edu.java.scrapper.clients.dataHandling.holder;

import edu.java.clients.dataHandling.ClientDataHandler;
import edu.java.clients.dataHandling.GitHubClientDataHandler;
import edu.java.clients.dataHandling.StackOverflowClientDataHandler;
import edu.java.clients.dataHandling.holder.ClientDataHandlersHolder;
import edu.java.models.GitHubEvents;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

public class ClientDataHandlersHolderTest {
    @Test
    @SneakyThrows
    public void getMessageByDescriptionAndHost_whenHostIsSupported_shouldReturnCorrectAnswer() {
        Properties properties = new Properties();
        GitHubEvents.EventsHolder eventsHolder = Mockito.mock(GitHubEvents.EventsHolder.class);
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        List<ClientDataHandler> handlerList = List.of(
            new GitHubClientDataHandler(properties, eventsHolder),
            new StackOverflowClientDataHandler(properties)
        );
        ClientDataHandlersHolder holder = new ClientDataHandlersHolder(handlerList, properties);
        String host = "stackoverflow.com";
        String actual = holder.getMessageByDescriptionAndHost(host, null);
        String expected = properties.getProperty("stackoverflow.event.default.updateMessage");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SneakyThrows
    public void getMessageByDescriptionAndHost_whenHostIsNotSupported_shouldReturnCorrectAnswer() {
        Properties properties = new Properties();
        GitHubEvents.EventsHolder eventsHolder = Mockito.mock(GitHubEvents.EventsHolder.class);
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        List<ClientDataHandler> handlerList = List.of(
            new GitHubClientDataHandler(properties, eventsHolder),
            new StackOverflowClientDataHandler(properties)
        );
        ClientDataHandlersHolder holder = new ClientDataHandlersHolder(handlerList, properties);
        String host = "stack.com";
        String actual = holder.getMessageByDescriptionAndHost(host, null);
        String expected = properties.getProperty("default.updateMessage");
        assertThat(actual).isEqualTo(expected);
    }
}
