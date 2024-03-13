package edu.java.domain;

import edu.java.api.dto.response.LinkResponse;
import edu.java.models.Chat;
import java.net.URI;
import java.util.List;

public interface ChatsToLinksDao {
    void add(long chatId, long linkId, URI uri);

    void remove(long chatId, long linkId, URI uri);

    List<LinkResponse> findAllByChatId(long chatId);

    boolean isLinkTrackedByAnybody(long linkId);

    List<LinkResponse> removeChatAndAllConnectedLinks(long chatId);

    List<Chat> getChatsForLink(long urlId);
}
