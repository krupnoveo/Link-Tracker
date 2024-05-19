package edu.java.api.services.jdbc;

import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import edu.java.api.services.LinksService;
import edu.java.clients.holder.ClientsHolder;
import edu.java.domain.ChatsToLinksRepository;
import edu.java.domain.LinksRepository;
import edu.java.models.Chat;
import edu.java.models.LinkData;
import edu.java.models.LinkDatabaseInformation;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class JdbcLinksService implements LinksService {
    private final LinksRepository linksRepository;
    private final ChatsToLinksRepository chatsToLinksRepository;
    private final ClientsHolder clientsHolder;

    @SneakyThrows
    @Override
    public ListLinksResponse getTrackedLinks(long chatId) {
        log.info("Getting tracked links...");
        return new ListLinksResponse(chatsToLinksRepository.findAllByChatId(chatId));
    }

    @Override
    public LinkResponse addLinkToTracking(long chatId, AddLinkRequest addLinkRequest) {
        log.info("Adding link...");
        URI uri = addLinkRequest.link();
        long linkId = linksRepository.add(uri, getLastUpdatedTimeFromLink(uri), OffsetDateTime.now());
        chatsToLinksRepository.add(chatId, linkId, uri);
        return new LinkResponse(linkId, uri);
    }

    @SneakyThrows
    @Override
    public LinkResponse removeLinkFromTracking(long chatId, RemoveLinkRequest removeLinkRequest) {
        log.info("Deleting link...");
        long linkId = removeLinkRequest.linkId();
        URI uri = linksRepository.getUriById(linkId);
        chatsToLinksRepository.remove(chatId, linkId, uri);
        if (!chatsToLinksRepository.isLinkTrackedByAnybody(linkId)) {
            linksRepository.remove(linkId);
        }
        return new LinkResponse(linkId, uri);
    }

    @Override
    public List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedForNminutes(int minutes) {
        return linksRepository.getAllLinksWhichWereNotCheckedBeforeDateTimeCriteria(
            OffsetDateTime.now().minusMinutes(minutes)
        );
    }

    @Override
    public void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId) {
        linksRepository.updateLinkInformationInDatabase(lastUpdated, lastChecked, urlId);
    }

    @Override
    public List<Chat> getChatsForLink(long urlId) {
        return chatsToLinksRepository.getChatsForLink(urlId);
    }

    private OffsetDateTime getLastUpdatedTimeFromLink(URI uri) {
        LinkData linkData = clientsHolder.checkURl(uri, null).get(0);
        if (linkData.url() != null && linkData.lastUpdated() != null) {
            return linkData.lastUpdated();
        }
        return null;
    }
}
