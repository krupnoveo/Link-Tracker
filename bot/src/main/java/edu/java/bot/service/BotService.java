package edu.java.bot.service;

import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.models.User;

public interface BotService {
    GenericResponse<Void> registerUser(User user);

    GenericResponse<AddLinkToDatabaseResponse> addLinkToDatabase(String url, long chatId);

    GenericResponse<RemoveLinkFromDatabaseResponse> removeLinkFromDatabase(long linkId, long chatId);

    GenericResponse<ListLinksResponse> listLinksFromDatabase(long chatId);
}
