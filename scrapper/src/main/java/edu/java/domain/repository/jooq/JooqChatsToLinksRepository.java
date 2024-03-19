package edu.java.domain.repository.jooq;

import edu.java.api.dto.response.LinkResponse;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.api.exceptions.LinkAlreadyTrackedException;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.domain.ChatsToLinksRepository;
import edu.java.models.Chat;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import static edu.java.domain.jooq.tables.ChatToLink.CHAT_TO_LINK;
import static edu.java.domain.jooq.tables.Link.LINK;


@Repository
@RequiredArgsConstructor
public class JooqChatsToLinksRepository implements ChatsToLinksRepository {
    private final DSLContext context;

    @Override
    public void add(long chatId, long linkId, URI uri) {
        if (context.select(CHAT_TO_LINK.CHAT_ID).from(CHAT_TO_LINK)
            .where(CHAT_TO_LINK.CHAT_ID.eq(chatId).and(CHAT_TO_LINK.LINK_ID.eq(linkId)))
            .fetch().isNotEmpty()
        ) {
            throw new LinkAlreadyTrackedException(uri.toString());
        }
        context.insertInto(CHAT_TO_LINK)
            .set(CHAT_TO_LINK.CHAT_ID, chatId)
            .set(CHAT_TO_LINK.LINK_ID, linkId)
            .execute();
    }

    @Override
    public void remove(long chatId, long linkId, URI uri) {
        if (context.select(CHAT_TO_LINK.CHAT_ID).from(CHAT_TO_LINK)
            .where(CHAT_TO_LINK.CHAT_ID.eq(chatId).and(CHAT_TO_LINK.LINK_ID.eq(linkId)))
            .fetch().isEmpty()
        ) {
            throw new LinkNotFoundException(uri.toString());
        }
        context.delete(CHAT_TO_LINK).where(CHAT_TO_LINK.CHAT_ID.eq(chatId).and(CHAT_TO_LINK.LINK_ID.eq(linkId)))
            .execute();
    }

    @Override
    public List<LinkResponse> findAllByChatId(long chatId) {
        return getLinksConnectedToChat(chatId);
    }

    @Override
    public boolean isLinkTrackedByAnybody(long linkId) {
        return isUrlTracked(linkId);
    }

    @Override
    public List<LinkResponse> removeChatAndAllConnectedLinks(long chatId) {
        if (context.select(CHAT_TO_LINK.LINK_ID).from(CHAT_TO_LINK)
            .where(CHAT_TO_LINK.CHAT_ID.eq(chatId)).fetch().isEmpty()
        ) {
            throw new ChatDoesNotExistException(chatId);
        }
        List<LinkResponse> links = getLinksConnectedToChat(chatId);
        context.delete(CHAT_TO_LINK).where(CHAT_TO_LINK.CHAT_ID.eq(chatId)).execute();
        return getLinksWhichNotTrackedByAnybody(links);
    }

    @Override
    public List<Chat> getChatsForLink(long urlId) {
        return context.select(CHAT_TO_LINK.CHAT_ID).from(CHAT_TO_LINK)
            .where(CHAT_TO_LINK.LINK_ID.eq(urlId)).fetchInto(Chat.class);
    }

    private List<LinkResponse> getLinksConnectedToChat(long chatId) {
        List<LinkResponse> links = new ArrayList<>();
        List<Long> linkIds = context.select(CHAT_TO_LINK.LINK_ID).from(CHAT_TO_LINK)
            .where(CHAT_TO_LINK.CHAT_ID.eq(chatId)).fetchInto(Long.class);
        for (Long linkId : linkIds) {
            links.add(
                context.select(LINK.ID, LINK.URL).from(LINK)
                    .where(LINK.ID.eq(linkId)).fetchSingleInto(LinkResponse.class)
            );
        }
        return links;
    }

    private boolean isUrlTracked(long linkId) {
        return context.select(CHAT_TO_LINK.CHAT_ID).from(CHAT_TO_LINK)
            .where(CHAT_TO_LINK.LINK_ID.eq(linkId)).fetch().isNotEmpty();
    }

    private List<LinkResponse> getLinksWhichNotTrackedByAnybody(List<LinkResponse> links) {
        List<LinkResponse> notTrackedLinks = new ArrayList<>();
        for (LinkResponse link : links) {
            if (!isUrlTracked(link.id())) {
                notTrackedLinks.add(link);
            }
        }
        return notTrackedLinks;
    }
}
