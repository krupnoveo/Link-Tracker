package edu.java.bot.api.endpoints.kafkaConsumer;

import edu.java.bot.api.service.LinkUpdatesService;
import edu.java.bot.models.ListLinkUpdates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final LinkUpdatesService service;

    @KafkaListener(topics = "linkUpdate", groupId = "group1")
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.FAIL_ON_ERROR, dltTopicSuffix = "_dlq")
    public void updateLinks(ListLinkUpdates linkUpdates) {
        service.notifyUsers(linkUpdates.linkUpdates());
    }

    @DltHandler
    public void handleError(ListLinkUpdates linkUpdates) {
        log.info("Ошибка обработки сообщения. Отправлено в очередь плохих сообщений");
    }
}
