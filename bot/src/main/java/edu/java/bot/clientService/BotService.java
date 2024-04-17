package edu.java.bot.clientService;

import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.Chat;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;

public interface BotService {
    GenericResponse<Void> registerUser(Chat chat);

    GenericResponse<AddLinkToDatabaseResponse> addLinkToDatabase(String url, long chatId);

    GenericResponse<RemoveLinkFromDatabaseResponse> removeLinkFromDatabase(long linkId, long chatId);

    GenericResponse<ListLinksResponse> listLinksFromDatabase(long chatId);
}
