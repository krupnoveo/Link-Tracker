package edu.java.domain.repository.jooq;

import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.domain.ChatsRepository;
import edu.java.models.Chat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.tables.Chat.CHAT;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "database.access-via", havingValue = "jooq")
public class JooqChatsRepository implements ChatsRepository {
    private final DSLContext context;

    @Override
    @Transactional
    public void add(long id) {
        if (isChatExists(id)) {
            throw new ChatAlreadyRegisteredException(id);
        }
        context.insertInto(CHAT).set(CHAT.CHAT_ID, id).execute();
    }

    @Override
    @Transactional
    public void remove(long id) {
        if (!isChatExists(id)) {
            throw new ChatDoesNotExistException(id);
        }
        context.delete(CHAT).where(CHAT.CHAT_ID.eq(id)).execute();
    }

    @Override
    @Transactional
    public List<Chat> findAll() {
        return context.select(CHAT.fields()).from(CHAT).fetchInto(Chat.class);
    }

    private boolean isChatExists(long id) {
        return context.select(CHAT.fields()).from(CHAT).where(CHAT.CHAT_ID.eq(id)).fetch().isNotEmpty();
    }
}
