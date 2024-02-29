package edu.java.bot.service;

import edu.java.bot.api.dto.request.AddLinkRequest;
import edu.java.bot.api.dto.request.RemoveLinkRequest;
import edu.java.bot.api.httpClient.ScrapperClient;
import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.models.User;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultBotService implements BotService {
    private final ScrapperClient scrapperClient;

    @Override
    public GenericResponse<Void> registerUser(User user) {
        return scrapperClient.registerChat(user);
    }

    @Override
    @SneakyThrows
    public GenericResponse<AddLinkToDatabaseResponse> addLinkToDatabase(String url, long chatId) {
        AddLinkRequest addLinkRequest = new AddLinkRequest(new URI(url));
        return scrapperClient.addLinkToTracking(chatId, addLinkRequest);
    }

    @Override
    @SneakyThrows
    public GenericResponse<RemoveLinkFromDatabaseResponse> removeLinkFromDatabase(long linkId, long chatId) {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(linkId);
        return scrapperClient.removeLinkFromTracking(chatId, removeLinkRequest);
    }

    @Override
    public GenericResponse<ListLinksResponse> listLinksFromDatabase(long chatId) {
        return scrapperClient.listLinks(chatId);
    }
}
