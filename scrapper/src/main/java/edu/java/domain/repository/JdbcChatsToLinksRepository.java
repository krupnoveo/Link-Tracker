package edu.java.domain.repository;

import edu.java.api.dto.response.LinkResponse;
import edu.java.api.exceptions.LinkAlreadyTrackedException;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.domain.ChatsToLinksDao;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcChatsToLinksRepository implements ChatsToLinksDao {
    private static final String SELECT_CHAT_ID_BY_CHAT_ID_AND_LINK_ID =
        "SELECT chat_id FROM chat_to_link WHERE chat_id=? AND link_id=?";
    private static final String INSERT_WITH_CHAT_ID_AND_LINK_ID =
        "INSERT INTO chat_to_link(chat_id, link_id) values(?, ?)";
    private static final String DELETE_BY_CHAT_ID_AND_LINK_ID =
        "DELETE FROM chat_to_link WHERE chat_id=? AND link_id=?";
    private static final String SELECT_LINK_ID_BY_CHAT_ID =
        "SELECT link_id FROM chat_to_link WHERE chat_id=?";
    private static final String SELECT_ID_AND_URL_BY_ID =
        "SELECT id, url FROM link WHERE id=?";
    private static final String SELECT_CHAT_ID_BY_LINK_ID =
        "SELECT chat_id FROM chat_to_link WHERE link_id=?";
    private static final String DELETE_BY_CHAT_ID =
        "DELETE FROM chat_to_link WHERE chat_id=?";
    private final JdbcClient client;

    @Autowired
    public JdbcChatsToLinksRepository(JdbcClient client) {
        this.client = client;
    }

    @Override
    @Transactional
    public void add(long chatId, long linkId, URI uri) {
        if (
            !client.sql(SELECT_CHAT_ID_BY_CHAT_ID_AND_LINK_ID)
                .params(
                    List.of(chatId, linkId)
                )
                .query(Long.class)
                .list()
                .isEmpty()
        ) {
            throw new LinkAlreadyTrackedException(uri.toString());
        }
        client.sql(INSERT_WITH_CHAT_ID_AND_LINK_ID)
            .params(
                List.of(chatId, linkId)
            )
            .update();
    }

    @Override
    @Transactional
    public void remove(long chatId, long linkId, URI uri) {
        if (
            client.sql(SELECT_CHAT_ID_BY_CHAT_ID_AND_LINK_ID)
            .params(
                List.of(chatId, linkId)
            )
            .query(Long.class)
            .list()
            .isEmpty()
        ) {
            throw new LinkNotFoundException(uri.toString());
        }
        client.sql(DELETE_BY_CHAT_ID_AND_LINK_ID)
            .params(
                List.of(chatId, linkId)
            )
            .update();
    }

    @Override
    @Transactional
    public List<LinkResponse> findAllByChatId(long chatId) {
        return getLinksConnectedToChat(chatId);
    }

    @Override
    @Transactional
    public boolean isLinkTrackedByAnybody(long linkId) {
        return isUrlTracked(linkId);
    }

    @Override
    @Transactional
    public List<LinkResponse> removeChatAndAllConnectedLinks(long chatId) {
        List<LinkResponse> links = getLinksConnectedToChat(chatId);
        client.sql(DELETE_BY_CHAT_ID)
            .param(chatId)
            .update();
        return getLinksWhichNotTrackedByAnybody(links);
    }

    private List<LinkResponse> getLinksConnectedToChat(long chatId) {
        List<LinkResponse> links = new ArrayList<>();
        List<Long> linkIds = client.sql(SELECT_LINK_ID_BY_CHAT_ID)
            .param(chatId)
            .query(Long.class)
            .list();
        for (Long linkId : linkIds) {
            links.add(
                client.sql(SELECT_ID_AND_URL_BY_ID)
                    .param(linkId)
                    .query(LinkResponse.class)
                    .single()
            );
        }
        return links;
    }

    private boolean isUrlTracked(long linkId) {
        return !client.sql(SELECT_CHAT_ID_BY_LINK_ID)
            .param(linkId)
            .query(Long.class)
            .list()
            .isEmpty();
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
