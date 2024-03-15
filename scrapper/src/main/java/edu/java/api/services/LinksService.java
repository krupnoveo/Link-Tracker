package edu.java.api.services;

import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;

public interface LinksService {
    ListLinksResponse getTrackedLinks(long chatId);

    LinkResponse addLinkToTracking(long chatId, AddLinkRequest addLinkRequest);

    LinkResponse removeLinkFromTracking(long chatId, RemoveLinkRequest removeLinkRequest);
}
