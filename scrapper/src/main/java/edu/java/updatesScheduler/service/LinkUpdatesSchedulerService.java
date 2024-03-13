package edu.java.updatesScheduler.service;

import edu.java.models.Chat;
import edu.java.models.LinkDatabaseInformation;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkUpdatesSchedulerService {
    List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedForNminutes(OffsetDateTime criteria);

    List<Chat> getChatsForLink(long urlId);

    OffsetDateTime getUpdatedTimeOfUrl(URI uri);

    void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId);
}
