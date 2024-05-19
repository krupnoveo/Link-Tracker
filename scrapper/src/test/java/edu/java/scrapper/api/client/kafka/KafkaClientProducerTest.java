package edu.java.scrapper.api.client.kafka;

import edu.java.api.client.kafka.KafkaClientProducer;
import edu.java.configuration.ApplicationConfig;
import edu.java.models.LinkUpdate;
import edu.java.models.ListLinkUpdates;
import edu.java.scrapper.IntegrationEnvironment;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = {
    "clients.github.token=1",
    "clients.stackoverflow.token=1",
    "clients.stackoverflow.key=1",
    "app.database-access-type=jpa"
})
public class KafkaClientProducerTest extends IntegrationEnvironment {
    @Autowired
    private KafkaTemplate<String, ListLinkUpdates> kafkaTemplate;

    @Autowired
    private ApplicationConfig config;

    @Test
    @DisplayName("Тест KafkaClientProducer.sendNotification()")
    public void sendNotification_shouldWorkCorrectly() {
        KafkaClientProducer linkUpdateSender = new KafkaClientProducer(kafkaTemplate, config);
        LinkUpdate linkUpdate = new LinkUpdate(
            1L,
            URI.create("http://test.com"),
            "",
            List.of(1L)
        );
        var kafkaConsumer = new KafkaConsumer<String, ListLinkUpdates>(
            Map.of(
                "bootstrap.servers", KAFKA.getBootstrapServers(),
                "group.id", "scrapper",
                "key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
                "value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer",
                "properties.spring.json.trusted.packages", "*",
                "spring.json.value.default.type", "edu.java.models.ListLinkUpdates",
                "auto.offset.reset", "earliest"
            )
        );
        kafkaConsumer.subscribe(List.of(config.kafka().topics().get("link-update")));
        linkUpdateSender.sendNotification(List.of(linkUpdate));
        await()
            .pollInterval(Duration.ofMillis(100))
            .atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> {
                var records = kafkaConsumer.poll(Duration.ofMillis(100));
                Assertions.assertThat(records).hasSize(1);
                Assertions.assertThat(records.iterator().next().value().linkUpdates()).isEqualTo(List.of(linkUpdate));
            });
    }
}
