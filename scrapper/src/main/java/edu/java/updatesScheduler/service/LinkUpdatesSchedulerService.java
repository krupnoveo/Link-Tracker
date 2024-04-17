package edu.java.updatesScheduler.service;

import edu.java.models.Chat;
import edu.java.models.LinkData;
import edu.java.models.LinkDatabaseInformation;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public interface LinkUpdatesSchedulerService {
    List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedForNminutes(int minutes);

    List<Chat> getChatsForLink(long urlId);

    List<LinkData> getLinkDataOfUrl(URI uri, OffsetDateTime lastUpdated);

    void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId);

    String getMessageByDescriptionAndHost(String host, Map<String, String> description);
}
