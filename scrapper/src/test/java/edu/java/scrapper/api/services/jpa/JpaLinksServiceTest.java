package edu.java.scrapper.api.services.jpa;

import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.api.exceptions.LinkAlreadyTrackedException;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.api.exceptions.UnsupportedUrlException;
import edu.java.api.services.LinksService;
import edu.java.models.Chat;
import edu.java.models.LinkDatabaseInformation;
import edu.java.scrapper.IntegrationEnvironment;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
    "clients.github.token=1",
    "clients.stackoverflow.token=1",
    "clients.stackoverflow.key=1",
    "app.database-access-type=jpa"
})
public class JpaLinksServiceTest extends IntegrationEnvironment {
    @Autowired
    private JdbcClient client;
    @Autowired
    private EntityManager manager;
    @Autowired
    private LinksService linksService;
    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void getTrackedLinks_shouldWorkCorrectly() {
        long chatId = 1L;
        long linkId1 = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            new URI("1").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
        )).query(Long.class).single();
        long linkId2 = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            new URI("2").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
        )).query(Long.class).single();
        client.sql("insert into chat(chat_id) values(?)").param(chatId).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId, linkId1
        )).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId, linkId2
        )).update();
        ListLinksResponse actual = linksService.getTrackedLinks(chatId);
        ListLinksResponse expected = new ListLinksResponse(List.of(
            new LinkResponse(linkId1, new URI("1")),
            new LinkResponse(linkId2, new URI("2"))
        ));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void addLinkToTracking_whenLinkWasNotTrackedBefore_and_isSupported_shouldWorkCorrectly() {
        long chatId = 1L;
        URI uri = new URI("https://github.com/krupnoveo/Link-Tracker");
        client.sql("insert into chat(chat_id) values(?)").param(chatId).update();
        LinkResponse response = linksService.addLinkToTracking(chatId, new AddLinkRequest(uri));
        manager.flush();
        assertThat(client.sql("select url from link where url=?").param(uri.toString()).query(URI.class).list()).isNotEmpty();
        assertThat(client.sql("select chat_id from chat_to_link where link_id=?").param(response.id()).query(Long.class).list()).isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void addLinkToTracking_whenLinkWasNotTrackedBefore_and_isNotSupported_shouldThrowUnsupportedUrlException() {
        long chatId = 1L;
        URI uri = new URI("https://github.com");
        client.sql("insert into chat(chat_id) values(?)").param(chatId).update();
        assertThrows(UnsupportedUrlException.class, () -> linksService.addLinkToTracking(chatId, new AddLinkRequest(uri)));
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void addLinkToTracking_whenLinkIsAlreadyTracked_shouldThrowLinkAlreadyTrackedException() {
        long chatId = 1L;
        URI uri = new URI("https://github.com");
        client.sql("insert into chat(chat_id) values(?)").param(chatId).update();
        long linkId = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            uri.toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
        )).query(Long.class).single();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId, linkId
        )).update();
        assertThrows(LinkAlreadyTrackedException.class, () -> linksService.addLinkToTracking(chatId, new AddLinkRequest(uri)));
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void removeLinkFromTracking_whenLinkHasMoreThanOneTrackingChats_shouldWorkCorrectly() {
        long chatId1 = 1L;
        long chatId2 = 2L;
        URI uri = new URI("https://github.com");
        long linkId = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            uri.toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
        )).query(Long.class).single();
        client.sql("insert into chat(chat_id) values(?)").param(chatId1).update();
        client.sql("insert into chat(chat_id) values(?)").param(chatId2).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId1, linkId
        )).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId2, linkId
        )).update();
        linksService.removeLinkFromTracking(chatId1, new RemoveLinkRequest(linkId));
        manager.flush();
        assertThat(client.sql("select chat_id from chat_to_link where chat_id=? and link_id=?")
            .params(List.of(chatId1, linkId)).query(Long.class).list()).isEmpty();
        assertThat(client.sql("select id from link where id=?").param(linkId).query(Long.class).list()).isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void removeLinkFromTracking_whenLinkOneTrackingChat_shouldWorkCorrectly() {
        long chatId = 1L;
        URI uri = new URI("https://github.com");
        long linkId = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            uri.toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
        )).query(Long.class).single();
        client.sql("insert into chat(chat_id) values(?)").param(chatId).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId, linkId
        )).update();
        linksService.removeLinkFromTracking(chatId, new RemoveLinkRequest(linkId));
        manager.flush();
        assertThat(client.sql("select chat_id from chat_to_link where chat_id=? and link_id=?")
            .params(List.of(chatId, linkId)).query(Long.class).list()).isEmpty();
        assertThat(client.sql("select id from link where id=?").param(linkId).query(Long.class).list()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void removeLinkFromTracking_whenLinkNotFound_shouldThrowLinkNotFoundException() {
        long chatId = 1L;
        URI uri = new URI("https://github.com");
        long linkId = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            uri.toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
        )).query(Long.class).single();
        client.sql("insert into chat(chat_id) values(?)").param(chatId).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId, linkId
        )).update();
        assertThrows(LinkNotFoundException.class, () -> {
            linksService.removeLinkFromTracking(chatId, new RemoveLinkRequest(linkId + 1));
        });
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void removeLinkFromTracking_whenChatNotFound_shouldThrowLinkNotFoundException() {
        long chatId = 1L;
        URI uri = new URI("https://github.com");
        long linkId = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            uri.toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
        )).query(Long.class).single();
        client.sql("insert into chat(chat_id) values(?)").param(chatId).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId, linkId
        )).update();
        assertThrows(ChatDoesNotExistException.class, () -> {
            linksService.removeLinkFromTracking(chatId + 1, new RemoveLinkRequest(linkId));
        });
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void getAllLinksWhichWereNotCheckedForNminutes() {
        OffsetDateTime time = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
        URI uri = new URI("https://github.com");
        long linkId1 = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            uri + "1", time.minusHours(1), time.minusMinutes(40)
        )).query(Long.class).single();
        long linkId2 = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            uri + "2", time.minusHours(2), time.minusMinutes(10)
        )).query(Long.class).single();
        List<LinkDatabaseInformation> actual = linksService.getAllLinksWhichWereNotCheckedForNminutes(20);
        List<LinkDatabaseInformation> expected = List.of(
            new LinkDatabaseInformation(linkId1, new URI(uri + "1"), time.minusHours(1))
        );
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void updateLinkInformationInDatabase_shouldWorkCorrectly() {
        URI uri = new URI("https://github.com");
        long linkId = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            uri.toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
        )).query(Long.class).single();
        OffsetDateTime time = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
        linksService.updateLinkInformationInDatabase(time, time, linkId);
        manager.flush();
        assertThat(client.sql("select id from link where updated_at=? and checked_at=? and id=?").params(List.of(time, time, linkId)).query(
            Long.class).list()).isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void getChatsForLink_shouldReturnCorrectResult() {
        long chatId1 = 1L;
        long chatId2 = 2L;
        URI uri = new URI("https://github.com");
        long linkId = client.sql("insert into link(url, updated_at, checked_at) values(?, ?, ?) returning id").params(List.of(
            uri.toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
        )).query(Long.class).single();
        client.sql("insert into chat(chat_id) values(?)").param(chatId1).update();
        client.sql("insert into chat(chat_id) values(?)").param(chatId2).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId1, linkId
        )).update();
        client.sql("insert into chat_to_link(chat_id, link_id) values(?, ?)").params(List.of(
            chatId2, linkId
        )).update();
        List<Chat> actual = linksService.getChatsForLink(linkId);
        List<Chat> expected = List.of(
            new Chat(chatId1),
            new Chat(chatId2)
        );
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
