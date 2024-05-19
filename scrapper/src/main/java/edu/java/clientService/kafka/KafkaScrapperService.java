package edu.java.clientService.kafka;

import edu.java.api.client.kafka.KafkaClientProducer;
import edu.java.clientService.ScrapperService;
import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaScrapperService implements ScrapperService {
    private final KafkaClientProducer clientProducer;

    @Override
    public GenericResponse<Void> notifyChats(List<LinkUpdate> linkUpdate) {
        clientProducer.sendNotification(linkUpdate);
        return new GenericResponse<>(null, null);
    }
}

