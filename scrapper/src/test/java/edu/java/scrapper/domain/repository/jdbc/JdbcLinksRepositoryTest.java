package edu.java.scrapper.domain.repository.jdbc;

import edu.java.api.dto.response.LinkResponse;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.domain.LinksRepository;
import edu.java.domain.repository.jdbc.JdbcLinksRepository;
import edu.java.models.LinkDatabaseInformation;
import edu.java.scrapper.IntegrationEnvironment;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
    "clients.github.token=1",
    "clients.stackoverflow.token=1",
    "clients.stackoverflow.key=1",
    "app.database-access-type=jdbc"
})
public class JdbcLinksRepositoryTest extends IntegrationEnvironment {
    @Autowired
    private LinksRepository linksRepository;
    @Autowired
    private JdbcClient client;

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void add_whenLinkIsNotTrackedYet_shouldWorkCorrectly() {
        long urlId = linksRepository.add(new URI(""), OffsetDateTime.MIN, OffsetDateTime.MIN);
        assertThat(client.sql("SELECT id FROM link WHERE id=?").param(urlId).query(Long.class).list()).isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void add_whenLinkIsAlreadyTracked_shouldNotThrowException() {
        long id = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
                )
            )
            .query(Long.class)
            .single();
        long urlId = linksRepository.add(new URI(""), OffsetDateTime.MIN, OffsetDateTime.MIN);
        assertThat(urlId).isEqualTo(id);
        assertThat(client.sql("SELECT id FROM link WHERE id=?").param(id).query(Long.class).list()).isNotEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void remove_whenLinkExists_shouldReturnCorrectAnswer() {
        long urlId = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("1").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
                )
            )
            .query(Long.class)
            .single();
        URI uri = linksRepository.remove(urlId);
        assertThat(uri).isEqualTo(new URI("1"));
        assertThat(client.sql("SELECT id FROM link WHERE id=?").param(urlId).query(Long.class).list()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void remove_whenLinkDoesNotExist_shouldThrowLinkNotFoundException() {
        LinkNotFoundException exception = assertThrows(LinkNotFoundException.class, () -> {
            linksRepository.remove(4000L);
        });
        assertThat(exception.getMessage()).isEqualTo("Ссылка не найдена");
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void findAll_shouldReturnCorrectAnswer() {
        long urlId1 = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("1").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
                )
            )
            .query(Long.class)
            .single();
        long urlId2 = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("2").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
                )
            )
            .query(Long.class)
            .single();
        List<LinkResponse> actual = linksRepository.findAll();
        List<LinkResponse> expected = List.of(
            new LinkResponse(urlId1, new URI("1")),
            new LinkResponse(urlId2, new URI("2"))
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void getUriById_shouldReturnCorrectAnswer() {
        long urlId = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("3").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
                )
            )
            .query(Long.class)
            .single();
        URI actual = linksRepository.getUriById(urlId);
        assertThat(actual).isEqualTo(new URI("3"));
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void removeAll_shouldWorkCorrectly() {
        long urlId1 = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("1").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
                )
            )
            .query(Long.class)
            .single();
        long urlId2 = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("2").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
                )
            )
            .query(Long.class)
            .single();
        List<LinkResponse> links = List.of(
            new LinkResponse(urlId1, new URI("1")),
            new LinkResponse(urlId2, new URI("2"))
        );
        linksRepository.removeAll(links);
        assertThat(client.sql("SELECT id FROM link").query(Long.class).list()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void getAllLinksWhichWereNotCheckedBeforeDateTimeCriteria_shouldReturnCorrectAnswer() {
        long urlId1 = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("1").toString(), OffsetDateTime.MIN, OffsetDateTime.MAX
                )
            )
            .query(Long.class)
            .single();
        long urlId2 = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("2").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
                )
            )
            .query(Long.class)
            .single();
        OffsetDateTime criteria = OffsetDateTime.of(2024, 3, 14, 21, 23, 0, 0, ZoneOffset.UTC);
        List<LinkDatabaseInformation> actual = linksRepository.getAllLinksWhichWereNotCheckedBeforeDateTimeCriteria(criteria);
        List<LinkDatabaseInformation> expected = List.of(
            new LinkDatabaseInformation(urlId2, new URI("2"), OffsetDateTime.MIN)
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    @SneakyThrows
    public void updateLinkInformationInDatabase_shouldWorkCorrectly() {
        long urlId1 = client.sql("INSERT INTO link(url, updated_at, checked_at) values(?,?,?) RETURNING id")
            .params(
                List.of(
                    new URI("1").toString(), OffsetDateTime.MIN, OffsetDateTime.MIN
                )
            )
            .query(Long.class)
            .single();
        OffsetDateTime updatedTime = OffsetDateTime.of(2024, 3, 14, 21, 23, 0, 0, ZoneOffset.UTC);
        linksRepository.updateLinkInformationInDatabase(updatedTime, updatedTime, urlId1);

        OffsetDateTime checkedAt = client.sql("SELECT checked_at FROM link WHERE id=?")
            .param(urlId1)
            .query(OffsetDateTime.class)
            .single();
        OffsetDateTime updatedAt = client.sql("SELECT updated_at FROM link WHERE id=?")
            .param(urlId1)
            .query(OffsetDateTime.class)
            .single();
        assertThat(updatedAt).isEqualTo(updatedTime);
        assertThat(checkedAt).isEqualTo(updatedTime);
    }
}
