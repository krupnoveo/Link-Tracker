package edu.java.scrapper.clients.dataHandling;

import edu.java.clients.dataHandling.StackOverflowClientDataHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

public class StackOverflowClientDataHandlerTest {
    @Test
    @SneakyThrows
    public void getMessageByDescription_shouldReturnCorrectResult() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        StackOverflowClientDataHandler handler = new StackOverflowClientDataHandler(properties);

        assertThat(handler.getMessageByDescription(null)).isEqualTo(properties.getProperty("stackoverflow.updateMessage"));
    }

    @Test
    @SneakyThrows
    public void isHostSupported_shouldReturnCorrectResult() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        StackOverflowClientDataHandler handler = new StackOverflowClientDataHandler(properties);

        String host1 = "stackoverflow.com";
        String host2 = "stackexchange.com";
        assertThat(handler.isHostSupported(host1)).isTrue();
        assertThat(handler.isHostSupported(host2)).isFalse();
    }
}
