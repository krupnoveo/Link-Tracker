package edu.java.scrapper.domain.repository.jooq;

import edu.java.api.dto.response.LinkResponse;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.api.exceptions.LinkAlreadyTrackedException;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.domain.ChatsToLinksRepository;
import edu.java.models.Chat;
import edu.java.scrapper.IntegrationEnvironment;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
    "clients.github.token=1",
    "clients.stackoverflow.token=1",
    "clients.stackoverflow.key=1",
    "database.access-via=jooq"
})
public class JooqChatsToLinksRepositoryTest extends IntegrationEnvironment {
    @Autowired
    private ChatsToLinksRepository chatsToLinksRepository;
    @Autowired
    private JdbcClient client;

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void add_whenLinkIsNotYetTrackedByChat_shouldWorkCorrectly() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        long urlId = client.sql("INSERT INTO link(url) values('test') RETURNING id").query(Long.class).single();
        chatsToLinksRepository.add(1L, urlId, new URI("test"));
        assertThat(client.sql("SELECT chat_id FROM chat_to_link WHERE chat_id=1 AND link_id=?").param(urlId).query(Long.class).list()).isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void add_whenLinkIsAlreadyTrackedByChat_shouldThrowLinkAlreadyTrackedException() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        long urlId = client.sql("INSERT INTO link(url) values('test') RETURNING id").query(Long.class).single();
        chatsToLinksRepository.add(1L, urlId, new URI("test"));
        LinkAlreadyTrackedException exception = assertThrows(LinkAlreadyTrackedException.class, () -> {
            chatsToLinksRepository.add(1L, urlId, new URI("test"));
        });
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void remove_whenLinkIsTrackedByChat_shouldWorkCorrectly() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        long urlId = client.sql("INSERT INTO link(url) values('test') RETURNING id").query(Long.class).single();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(1, ?)").param(urlId).update();
        chatsToLinksRepository.remove(1, urlId, new URI("test"));
        assertThat(client.sql("SELECT link_id FROM chat_to_link WHERE link_id=?").param(urlId).query(Long.class).list()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void remove_whenLinkIsNotTrackedByChat_shouldThrowLinkNotFoundException() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        long urlId = client.sql("INSERT INTO link(url) values('test') RETURNING id").query(Long.class).single();
        LinkNotFoundException exception = assertThrows(LinkNotFoundException.class, () -> {
            chatsToLinksRepository.remove(1, urlId, new URI("test"));
        });

        assertThat(exception.getMessage()).isEqualTo("Ссылка %s не найдена".formatted("test"));
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void findAllByChatId_shouldReturnCorrectAnswer() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        long urlId1 = client.sql("INSERT INTO link(url) values('test1') RETURNING id").query(Long.class).single();
        long urlId2 = client.sql("INSERT INTO link(url) values('test2') RETURNING id").query(Long.class).single();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(1, ?)").param(urlId1).update();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(1, ?)").param(urlId2).update();
        List<LinkResponse> actual = chatsToLinksRepository.findAllByChatId(1L);
        List<LinkResponse> expected = List.of(
            new LinkResponse(urlId1, new URI("test1")),
            new LinkResponse(urlId2, new URI("test2"))
        );
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void isLinkTrackedByAnybody_whenLinkIsTrackedBySomeone_shouldReturnCorrectAnswer() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        long urlId = client.sql("INSERT INTO link(url) values('test1') RETURNING id").query(Long.class).single();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(1, ?)").param(urlId).update();
        assertThat(chatsToLinksRepository.isLinkTrackedByAnybody(urlId)).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void isLinkTrackedByAnybody_whenLinkIsNotTrackedByAnybody_shouldReturnCorrectAnswer() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        long urlId1 = client.sql("INSERT INTO link(url) values('test1') RETURNING id").query(Long.class).single();
        long urlId2 = client.sql("INSERT INTO link(url) values('test2') RETURNING id").query(Long.class).single();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(1, ?)").param(urlId1).update();
        assertThat(chatsToLinksRepository.isLinkTrackedByAnybody(urlId2)).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void getChatsForLink_shouldReturnCorrectAnswer() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        client.sql("INSERT INTO chat(chat_id) values(2)").update();
        long urlId = client.sql("INSERT INTO link(url) values('test1') RETURNING id").query(Long.class).single();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(1, ?)").param(urlId).update();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(2, ?)").param(urlId).update();
        List<Chat> actual = chatsToLinksRepository.getChatsForLink(urlId);
        List<Chat> expected = List.of(
            new Chat(1L),
            new Chat(2L)
        );
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void removeChatAndAllConnectedLinks_whenChatExists_shouldReturnCorrectAnswer() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        client.sql("INSERT INTO chat(chat_id) values(2)").update();
        long urlId1 = client.sql("INSERT INTO link(url) values('test1') RETURNING id").query(Long.class).single();
        long urlId2 = client.sql("INSERT INTO link(url) values('test2') RETURNING id").query(Long.class).single();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(1, ?)").param(urlId1).update();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(2, ?)").param(urlId1).update();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(2, ?)").param(urlId2).update();
        List<LinkResponse> actual = chatsToLinksRepository.removeChatAndAllConnectedLinks(2L);
        List<LinkResponse> expected = List.of(
            new LinkResponse(urlId2, new URI("test2"))
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void removeChatAndAllConnectedLinks_whenChatDoesNotExist_shouldThrowChatDoesNotExistException() {
        client.sql("INSERT INTO chat(chat_id) values(1)").update();
        client.sql("INSERT INTO chat(chat_id) values(2)").update();
        long urlId1 = client.sql("INSERT INTO link(url) values('test1') RETURNING id").query(Long.class).single();
        client.sql("INSERT INTO chat_to_link(chat_id, link_id) values(1, ?)").param(urlId1).update();
        ChatDoesNotExistException exception = assertThrows(ChatDoesNotExistException.class, () -> {
            chatsToLinksRepository.removeChatAndAllConnectedLinks(2L);
        });

        assertThat(exception.getMessage()).isEqualTo("Чат %s не существует".formatted(2L));
    }
}
