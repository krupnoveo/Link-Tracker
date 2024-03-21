package edu.java.domain.repository.jooq;

import edu.java.api.dto.response.LinkResponse;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.domain.LinksRepository;
import edu.java.models.LinkDatabaseInformation;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.tables.Link.LINK;

@RequiredArgsConstructor
public class JooqLinksRepository implements LinksRepository {
    private final DSLContext context;

    @Override
    @Transactional
    public long add(URI uri, OffsetDateTime lastUpdated, OffsetDateTime lastChecked) {
        if (context.select(LINK.ID).from(LINK).where(LINK.URL.eq(uri.toString())).fetch().isEmpty()) {
            context.insertInto(LINK)
                .set(LINK.URL, uri.toString())
                .set(LINK.UPDATED_AT, lastUpdated)
                .set(LINK.CHECKED_AT, lastChecked)
                .execute();
        }
        return context.select(LINK.ID).from(LINK).where(LINK.URL.eq(uri.toString())).fetchSingleInto(Long.class);
    }

    @Override
    @Transactional
    public URI remove(long id) {
        if (context.select(LINK.URL).from(LINK).where(LINK.ID.eq(id)).fetch().isEmpty()) {
            throw new LinkNotFoundException();
        }
        URI uri = context.select(LINK.URL).from(LINK).where(LINK.ID.eq(id)).fetchSingleInto(URI.class);
        context.delete(LINK).where(LINK.ID.eq(id)).execute();
        return uri;
    }

    @Override
    @Transactional
    public List<LinkResponse> findAll() {
        return context.select(LINK.ID, LINK.URL).from(LINK).fetchInto(LinkResponse.class);
    }

    @Override
    @Transactional
    public URI getUriById(long id) {
        if (context.select(LINK.URL).from(LINK).where(LINK.ID.eq(id)).fetch().isEmpty()) {
            throw new LinkNotFoundException();
        }
        return context.select(LINK.URL).from(LINK).where(LINK.ID.eq(id)).fetchSingleInto(URI.class);
    }

    @Override
    @Transactional
    public void removeAll(List<LinkResponse> notTrackedLinks) {
        context.delete(LINK).where(LINK.ID.in(notTrackedLinks.stream().map(LinkResponse::id).toList())).execute();
    }

    @Override
    @Transactional
    public List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedBeforeDateTimeCriteria(OffsetDateTime criteria) {
        return context.select(LINK.ID.as("urlId"), LINK.URL, LINK.UPDATED_AT.as("lastUpdated")).from(LINK)
            .where(LINK.CHECKED_AT.lessThan(criteria))
            .orderBy(LINK.CHECKED_AT).fetchInto(LinkDatabaseInformation.class);
    }

    @Override
    @Transactional
    public void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId) {
        context.update(LINK)
            .set(LINK.UPDATED_AT, lastUpdated)
            .set(LINK.CHECKED_AT, lastChecked)
            .where(LINK.ID.eq(urlId))
            .execute();
    }
}
