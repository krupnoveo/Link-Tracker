package edu.java.bot.service;

import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.models.User;
import java.util.UUID;

public interface BotService {
    boolean registerUser(User user);

    AddLinkToDatabaseResponse addLinkToDatabase(String url, Long userId);

    RemoveLinkFromDatabaseResponse removeLinkFromDatabase(UUID uuid, Long userId);

    ListLinksResponse listLinksFromDatabase(Long userId);
}
