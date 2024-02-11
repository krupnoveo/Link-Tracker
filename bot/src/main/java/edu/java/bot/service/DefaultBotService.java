package edu.java.bot.service;

import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.Link;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.models.User;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DefaultBotService implements BotService {
    @Override
    public boolean registerUser(User user) {
        //stub
        return true;
    }

    @Override
    public AddLinkToDatabaseResponse addLinkToDatabase(String url, Long userId) {
        //stub
        return new AddLinkToDatabaseResponse(true, "такая ссылка уже есть");
    }

    @Override
    public RemoveLinkFromDatabaseResponse removeLinkFromDatabase(UUID uuid, Long userId) {
        //stub
        return new RemoveLinkFromDatabaseResponse(true, "такой ссылки нет в списке добавленных");
    }

    @Override
    public ListLinksResponse listLinksFromDatabase(Long userId) {
        //stub
        return new ListLinksResponse(
            List.of(
                new Link("https://github.com/krupnoveo/Link-Tracker", UUID.randomUUID()),
                new Link("https://stackoverflow.com/questions/77975883/spring-security-getting-oauth2-token-from-authorization-server-using-secret-jwt", UUID.randomUUID())
            )
        );
    }
}
