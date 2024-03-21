package edu.java.api.services;

import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import edu.java.models.Chat;
import edu.java.models.LinkDatabaseInformation;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinksService {
    ListLinksResponse getTrackedLinks(long chatId);

    LinkResponse addLinkToTracking(long chatId, AddLinkRequest addLinkRequest);

    LinkResponse removeLinkFromTracking(long chatId, RemoveLinkRequest removeLinkRequest);

    List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedForNminutes(int minutes);

    void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId);

    List<Chat> getChatsForLink(long urlId);
}
