package edu.java.domain.repository;

import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.domain.ChatsDao;
import edu.java.models.Chat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcChatRepository implements ChatsDao {
    private static final String SELECT_BY_ID = "SELECT * FROM chat WHERE chat_id=?";
    private static final String SELECT_BY_ALL = "SELECT * FROM chat";
    private static final String INSERT_WITH_CHAT_ID = "INSERT INTO chat(chat_id) values(?)";
    private static final String DELETE_BY_ID = "DELETE FROM chat WHERE chat_id=?";
    private final JdbcClient client;

    @Autowired
    public JdbcChatRepository(JdbcClient client) {
        this.client = client;
    }

    @Override
    @Transactional
    public void add(long chatId) {
        if (
            !client.sql(SELECT_BY_ID)
                .param(chatId)
                .query(Chat.class)
                .list()
                .isEmpty()
        ) {
            throw new ChatAlreadyRegisteredException(chatId);
        }
        client.sql(INSERT_WITH_CHAT_ID)
            .param(chatId)
            .update();
    }

    @Override
    @Transactional
    public void remove(long chatId) {
        if (
            client.sql(SELECT_BY_ID)
                .param(chatId)
                .query(Chat.class)
                .list()
                .isEmpty()
        ) {
            throw new ChatDoesNotExistException(chatId);
        }
        client.sql(DELETE_BY_ID)
            .param(chatId)
            .update();
    }

    @Override
    @Transactional
    public List<Chat> findAll() {
        return client.sql(SELECT_BY_ALL)
            .query(Chat.class)
            .list();
    }
}
