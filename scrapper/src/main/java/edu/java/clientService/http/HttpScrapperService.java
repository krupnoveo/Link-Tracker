package edu.java.clientService.http;

import edu.java.api.client.http.BotClient;
import edu.java.clientService.ScrapperService;
import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HttpScrapperService implements ScrapperService {
    private final BotClient botClient;

    @Override
    public GenericResponse<Void> notifyChats(List<LinkUpdate> linkUpdates) {
        return botClient.sendNotification(linkUpdates);
    }
}
