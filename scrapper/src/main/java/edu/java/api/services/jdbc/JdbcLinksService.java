package edu.java.api.services.jdbc;

import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import edu.java.api.services.LinksService;
import edu.java.clientsHolder.ClientsHolder;
import edu.java.domain.repository.JdbcChatsToLinksRepository;
import edu.java.domain.repository.JdbcLinksRepository;
import edu.java.models.LinkData;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class JdbcLinksService implements LinksService {
    private final JdbcLinksRepository jdbcLinksRepository;
    private final JdbcChatsToLinksRepository jdbcChatsToLinksRepository;
    private final ClientsHolder clientsHolder;

    @SneakyThrows
    public ListLinksResponse getTrackedLinks(long chatId) {
        log.info("Getting tracked links...");
        return new ListLinksResponse(jdbcChatsToLinksRepository.findAllByChatId(chatId));
    }

    public LinkResponse addLinkToTracking(long chatId, AddLinkRequest addLinkRequest) {
        log.info("Adding link...");
        URI uri = addLinkRequest.link();
        long linkId = jdbcLinksRepository.add(uri, getOffsetDateTimeFromLink(uri), OffsetDateTime.now());
        jdbcChatsToLinksRepository.add(chatId, linkId, uri);
        return new LinkResponse(linkId, uri);
    }

    @SneakyThrows
    public LinkResponse removeLinkFromTracking(long chatId, RemoveLinkRequest removeLinkRequest) {
        log.info("Deleting link...");
        long linkId = removeLinkRequest.linkId();
        URI uri = jdbcLinksRepository.getUriById(linkId);
        jdbcChatsToLinksRepository.remove(chatId, linkId, uri);
        if (!jdbcChatsToLinksRepository.isLinkTrackedByAnybody(linkId)) {
            jdbcLinksRepository.remove(linkId);
        }
        return new LinkResponse(linkId, uri);
    }

    private OffsetDateTime getOffsetDateTimeFromLink(URI uri) {
        LinkData linkData = clientsHolder.checkURl(uri);
        if (linkData.url() != null && linkData.lastUpdated() != null) {
            return linkData.lastUpdated();
        }
        return null;
    }
}
