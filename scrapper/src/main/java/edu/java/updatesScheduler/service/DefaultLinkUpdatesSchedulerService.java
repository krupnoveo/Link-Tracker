package edu.java.updatesScheduler.service;

import edu.java.api.services.LinksService;
import edu.java.clients.dataHandling.holder.ClientDataHandlersHolder;
import edu.java.clients.holder.ClientsHolder;
import edu.java.models.Chat;
import edu.java.models.LinkData;
import edu.java.models.LinkDatabaseInformation;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultLinkUpdatesSchedulerService implements LinkUpdatesSchedulerService {
    private final LinksService linksService;
    private final ClientsHolder clientsHolder;
    private final ClientDataHandlersHolder clientDataHandlersHolder;

    @Override
    public List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedForNminutes(int minutes) {
        return linksService.getAllLinksWhichWereNotCheckedForNminutes(minutes);
    }

    @Override
    public List<Chat> getChatsForLink(long urlId) {
        return linksService.getChatsForLink(urlId);
    }

    @Override
    public List<LinkData> getLinkDataOfUrl(URI uri, OffsetDateTime lastUpdated) {
        return clientsHolder.checkURl(uri, lastUpdated);
    }

    @Override
    public void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId) {
        linksService.updateLinkInformationInDatabase(lastUpdated, lastChecked, urlId);
    }

    @Override
    public String getMessageByDescriptionAndHost(String host, Map<String, String> description) {
        return clientDataHandlersHolder.getMessageByDescriptionAndHost(host, description);
    }
}
