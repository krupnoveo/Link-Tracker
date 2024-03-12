package edu.java.clientService;

import edu.java.api.httpClient.BotClient;
import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultScrapperService implements ScrapperService {
    private final BotClient botClient;

    @Override
    public GenericResponse<Void> updateLinks(LinkUpdate linkUpdate) {
        return botClient.updateLinks(linkUpdate);
    }
}