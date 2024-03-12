package edu.java.domain;

import edu.java.api.dto.response.LinkResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinksDao {
    long add(URI uri, OffsetDateTime lastUpdated, OffsetDateTime lastChecked);

    URI remove(long id);

    List<LinkResponse> findAll();

    URI getUriById(long id);

    void removeAll(List<LinkResponse> notTrackedLinks);
}
