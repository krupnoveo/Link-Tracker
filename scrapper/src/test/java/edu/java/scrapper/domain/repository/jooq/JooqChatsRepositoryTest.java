package edu.java.scrapper.domain.repository.jooq;

import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.domain.ChatsRepository;
import edu.java.models.Chat;
import edu.java.scrapper.IntegrationEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
    "clients.github.token=1",
    "clients.stackoverflow.token=1",
    "clients.stackoverflow.key=1",
    "app.database-access-type=jooq"
})
public class JooqChatsRepositoryTest extends IntegrationEnvironment {
    @Autowired
    private ChatsRepository chatRepository;
    @Autowired
    private JdbcClient client;

    @Test
    @Transactional
    @Rollback
    public void add_whenChatDoesNotExistAlready_shouldWorkCorrectly() {
        chatRepository.add(1L);
        assertThat(client.sql("SELECT * FROM chat WHERE chat_id=1").query(Long.class).list()).isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void add_whenChatExistsAlready_shouldThrowChatAlreadyRegisteredException() {
        long chatId = 1L;
        client.sql("INSERT INTO chat(chat_id) values(?)").param(chatId).update();
        ChatAlreadyRegisteredException exception = assertThrows(ChatAlreadyRegisteredException.class, () -> {
            chatRepository.add(chatId);
        });
        assertThat(exception.getMessage()).isEqualTo("Чат %d уже зарегистрирован".formatted(chatId));
    }

    @Test
    @Transactional
    @Rollback
    public void remove_whenChatExists_shouldWorkCorrectly() {
        long chatId = 1L;
        client.sql("INSERT INTO chat(chat_id) values(?)").param(chatId).update();
        chatRepository.remove(chatId);
        assertThat(client.sql("SELECT * FROM chat").query(Long.class).list()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void remove_whenChatDoesNotExist_shouldThrowChatDoesNotExistException() {
        long chatId = 1L;
        ChatDoesNotExistException exception = assertThrows(ChatDoesNotExistException.class, () -> {
            chatRepository.remove(chatId);
        });
        assertThat(exception.getMessage()).isEqualTo("Чат %d не существует".formatted(chatId));
    }

    @Test
    @Transactional
    @Rollback
    public void findAll_shouldWorkCorrectly() {
        long chatId1 = 1L;
        long chatId2 = 2L;
        Chat chat1 = new Chat(chatId1);
        Chat chat2 = new Chat(chatId2);
        client.sql("INSERT INTO chat(chat_id) values(?)").param(chatId1).update();
        client.sql("INSERT INTO chat(chat_id) values(?)").param(chatId2).update();
        List<Chat> actual = chatRepository.findAll();
        List<Chat> expected = List.of(chat1, chat2);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
