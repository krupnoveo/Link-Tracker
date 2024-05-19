package edu.java.api.client.kafka;

import edu.java.configuration.ApplicationConfig;
import edu.java.models.LinkUpdate;
import edu.java.models.ListLinkUpdates;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaClientProducer {
    private final KafkaTemplate<String, ListLinkUpdates> kafkaTemplate;
    private final ApplicationConfig config;

    public void sendNotification(List<LinkUpdate> linkUpdates) {
        kafkaTemplate.send(config.kafka().topics().get("link-update"), new ListLinkUpdates(linkUpdates));
    }
}
