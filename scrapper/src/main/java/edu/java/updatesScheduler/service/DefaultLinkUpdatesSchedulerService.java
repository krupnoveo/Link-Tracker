package edu.java.updatesScheduler.service;

import edu.java.clients.dataHandling.holder.ClientDataHandlersHolder;
import edu.java.clients.holder.ClientsHolder;
import edu.java.domain.ChatsToLinksRepository;
import edu.java.domain.LinksRepository;
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
    private final LinksRepository linksRepository;
    private final ChatsToLinksRepository chatsToLinksRepository;
    private final ClientsHolder clientsHolder;
    private final ClientDataHandlersHolder clientDataHandlersHolder;

    @Override
    public List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedForNminutes(int minutes) {
        return linksRepository.getAllLinksWhichWereNotCheckedBeforeDateTimeCriteria(
            OffsetDateTime.now().minusMinutes(minutes)
        );
    }

    @Override
    public List<Chat> getChatsForLink(long urlId) {
        return chatsToLinksRepository.getChatsForLink(urlId);
    }

    @Override
    public List<LinkData> getLinkDataOfUrl(URI uri, OffsetDateTime lastUpdated) {
        return clientsHolder.checkURl(uri, lastUpdated);
    }

    @Override
    public void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId) {
        linksRepository.updateLinkInformationInDatabase(lastUpdated, lastChecked, urlId);
    }

    @Override
    public String getMessageByDescriptionAndHost(String host, Map<String, String> description) {
        return clientDataHandlersHolder.getMessageByDescriptionAndHost(host, description);
    }
}
