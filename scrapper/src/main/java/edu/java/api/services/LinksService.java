package edu.java.api.services;

import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class LinksService {
    @SneakyThrows
    public ListLinksResponse getTrackedLinks(long chatId) {
        log.info("Getting tracked links...");
        return new ListLinksResponse(
            List.of(
                new LinkResponse(1, new URI("ya.ru")),
                new LinkResponse(2, new URI("google.com"))
            )
        );
    }

    public LinkResponse addLinkToTracking(long chatId, AddLinkRequest addLinkRequest) {
        log.info("Adding link...");
        return new LinkResponse(1L, addLinkRequest.link());
    }

    @SneakyThrows
    public LinkResponse removeLinkFromTracking(long chatId, RemoveLinkRequest removeLinkRequest) {
        log.info("Deleting link...");
        return new LinkResponse(removeLinkRequest.linkId(), new URI("test"));
    }
}
