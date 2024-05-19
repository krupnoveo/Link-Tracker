package edu.java.bot.api.endpoints.kafkaConsumer;

import edu.java.bot.IntegrationEnvironment;
import edu.java.bot.api.dto.request.LinkUpdate;
import edu.java.bot.api.service.LinkUpdatesService;
import edu.java.bot.models.ListLinkUpdates;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class KafkaConsumerTest extends IntegrationEnvironment {
    @MockBean
    private LinkUpdatesService linkUpdatesService;

    @Autowired
    private KafkaTemplate<String, ListLinkUpdates> kafkaTemplate;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Test
    @DisplayName("Тест KafkaUpdatesListener.updateLinks() с корректными данными")
    public void updateLinks_shouldReceiveMessageAndProcessIt() {
        LinkUpdate linkUpdate = new LinkUpdate(
            1L,
            URI.create("http://test.com"),
            "",
            List.of(1L)
        );
        kafkaTemplate.send("linkUpdate", new ListLinkUpdates(List.of(linkUpdate)));
        await()
            .pollInterval(Duration.ofMillis(100))
            .atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> Mockito.verify(linkUpdatesService, Mockito.times(1))
                .notifyUsers(List.of(linkUpdate)));
    }

    @Test
    @DisplayName("Тест KafkaUpdatesListener.updateLinks() с некорректной обработкой обновления")
    public void updateLinks_shouldRedirectMessageInDLQ_when_messageCantBeProcessed() {
        var linkUpdate = new LinkUpdate(
            1L,
            URI.create("http://test.com"),
            "test",
            List.of(1L)
        );
        Mockito.doThrow(RuntimeException.class).when(linkUpdatesService).notifyUsers(List.of(linkUpdate));

        KafkaConsumer<String, ListLinkUpdates> dlqKafkaConsumer = new KafkaConsumer<>(
            kafkaProperties.buildConsumerProperties(null)
        );
        dlqKafkaConsumer.subscribe(List.of("linkUpdate_dlq"));
        kafkaTemplate.send("linkUpdate", new ListLinkUpdates(List.of(linkUpdate)));
        await()
            .pollInterval(Duration.ofMillis(100))
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                var values = dlqKafkaConsumer.poll(Duration.ofMillis(100));
                Assertions.assertThat(values).hasSize(1);
                Assertions.assertThat(values.iterator().next().value().linkUpdates()).isEqualTo(List.of(linkUpdate));
                Mockito.verify(linkUpdatesService).notifyUsers(List.of(linkUpdate));
            });
    }
}
