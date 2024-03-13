package edu.java.domain.repository;

import edu.java.api.dto.response.LinkResponse;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.domain.LinksDao;
import edu.java.models.LinkDatabaseInformation;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcLinksRepository implements LinksDao {
    private static final String SELECT_ID_BY_URL = "SELECT id FROM link WHERE url=?";
    private static final String SELECT_URL_BY_ID = "SELECT url FROM link WHERE id=?";
    private static final String SELECT_ID_AND_URL = "SELECT id, url FROM link";
    private static final String INSERT_WITH_URL_AND_UPDATED_AT_AND_CHECKED_AT =
        "INSERT INTO link(url, updated_at, checked_at) values(?,?,?)";
    private static final String DELETE_BY_ID = "DELETE FROM link WHERE id=?";

    private static final String SELECT_ID_URL_UPDATED_AT_BY_TIME_CRITERIA =
        "SELECT id as urlId,url,updated_at as lastUpdated FROM link WHERE checked_at < ? ORDER BY checked_at";
    private static final String UPDATE_UPDATED_AT_LAST_CHECKED = """
        UPDATE link
        SET updated_at=?, checked_at=?
        WHERE id=?;
        """;
    private final JdbcClient client;

    @Autowired
    public JdbcLinksRepository(JdbcClient client) {
        this.client = client;
    }

    @Override
    @Transactional
    public long add(URI uri, OffsetDateTime lastUpdated, OffsetDateTime lastChecked) {
        if (
            client.sql(SELECT_ID_BY_URL)
                .param(uri.toString())
                .query(Long.class)
                .list()
                .isEmpty()
        ) {
            client.sql(INSERT_WITH_URL_AND_UPDATED_AT_AND_CHECKED_AT)
                .params(
                    List.of(uri.toString(), lastUpdated, lastChecked)
                )
                .update();
        }
        return client.sql(SELECT_ID_BY_URL)
            .param(uri.toString())
            .query(Long.class)
            .single();
    }

    @Override
    @Transactional
    public URI remove(long id) {
        if (
            client.sql(SELECT_URL_BY_ID)
                .param(id)
                .query(URI.class)
                .list()
                .isEmpty()
        ) {
            throw new LinkNotFoundException();
        }
        URI uri = client.sql(SELECT_URL_BY_ID)
            .param(id)
            .query(URI.class)
            .single();
        client.sql(DELETE_BY_ID)
            .param(id)
            .update();
        return uri;
    }

    @Override
    @Transactional
    public List<LinkResponse> findAll() {
        return client.sql(SELECT_ID_AND_URL)
            .query(LinkResponse.class)
            .list();
    }

    @Override
    @Transactional
    public URI getUriById(long id) {
        return client.sql(SELECT_URL_BY_ID)
            .param(id)
            .query(URI.class)
            .single();
    }

    @Override
    @Transactional
    public void removeAll(List<LinkResponse> notTrackedLinks) {
        for (LinkResponse link : notTrackedLinks) {
            client.sql(DELETE_BY_ID)
                .param(link.id())
                .update();
        }
    }

    @Override
    @Transactional
    public List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedForNminutes(OffsetDateTime criteria) {
        return client.sql(SELECT_ID_URL_UPDATED_AT_BY_TIME_CRITERIA)
            .param(criteria)
            .query(LinkDatabaseInformation.class)
            .list();
    }

    @Override
    @Transactional
    public void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId) {
        client.sql(UPDATE_UPDATED_AT_LAST_CHECKED)
            .params(List.of(
                lastUpdated,
                lastChecked,
                urlId
            ))
            .update();
    }
}
