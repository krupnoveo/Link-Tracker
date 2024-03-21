package edu.java.scrapper.api.services.jpa;

import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.api.services.ChatsService;
import edu.java.scrapper.IntegrationEnvironment;
import jakarta.persistence.EntityManager;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
    "clients.github.token=1",
    "clients.stackoverflow.token=1",
    "clients.stackoverflow.key=1",
    "app.database-access-type=jpa"
})
public class JpaChatsServiceTest extends IntegrationEnvironment {
    @Autowired
    private JdbcClient client;
    @Autowired
    private ChatsService chatsService;
    @Autowired
    private EntityManager manager;
    @Test
    @Transactional
    @Rollback
    public void registerChat_whenChatIsNotRegisteredYet_shouldWorkCorrectly() {
        long chatId = 1L;
        assertThat(client.sql("select chat_id from chat where chat_id=?").param(chatId).query(Long.class).list()).isEmpty();
        chatsService.registerChat(chatId);
        manager.flush();
        assertThat(client.sql("select chat_id from chat where chat_id=?").param(chatId).query(Long.class).list()).isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void registerChat_whenChatIsAlreadyRegistered_shouldThrowChatAlreadyRegisteredException() {
        chatsService.registerChat(1L);
        ChatAlreadyRegisteredException exception = assertThrows(ChatAlreadyRegisteredException.class, () -> {
            chatsService.registerChat(1L);
        });
        assertThat(exception.getMessage()).isEqualTo("Чат 1 уже зарегистрирован");
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void deleteChat_whenChatExists_shouldWorkCorrectly() {
        long chatId = 1L;
        long linkId1 = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id")
            .params(List.of(new URI("1").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN)).query(Long.class).single();
        long linkId2 = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id")
            .params(List.of(new URI("2").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN)).query(Long.class).single();
        client.sql("insert into chat(chat_id) values(?)").param(chatId).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(chatId, linkId1)).update();
        chatsService.deleteChat(chatId);
        manager.flush();
        assertThat(client.sql("select chat_id from chat where chat_id=?").param(chatId).query(Long.class).list()).isEmpty();
        assertThat(client.sql("select id from link where id=?").param(linkId1).query(Long.class).list()).isEmpty();
        assertThat(client.sql("select id from link where id=?").param(linkId2).query(Long.class).list()).isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void deleteChat_whenChatDoesNotExist_shouldThrowChatDoesNotExistException() {
        long chatId = 1L;
        assertThrows(ChatDoesNotExistException.class, () -> {
            chatsService.deleteChat(chatId);
        });
    }
}
