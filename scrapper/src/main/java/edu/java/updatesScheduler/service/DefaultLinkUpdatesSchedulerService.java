package edu.java.updatesScheduler.service;

import edu.java.clientsHolder.ClientsHolder;
import edu.java.domain.ChatsToLinksDao;
import edu.java.domain.LinksDao;
import edu.java.models.Chat;
import edu.java.models.LinkDatabaseInformation;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultLinkUpdatesSchedulerService implements LinkUpdatesSchedulerService {
    private final LinksDao linksRepository;
    private final ChatsToLinksDao chatsToLinksRepository;
    private final ClientsHolder clientsHolder;

    @Override
    public List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedForNminutes(OffsetDateTime criteria) {
        return linksRepository.getAllLinksWhichWereNotCheckedForNminutes(criteria);
    }

    @Override
    public List<Chat> getChatsForLink(long urlId) {
        return chatsToLinksRepository.getChatsForLink(urlId);
    }

    @Override
    public OffsetDateTime getUpdatedTimeOfUrl(URI uri) {
        return clientsHolder.checkURl(uri).lastUpdated();
    }

    @Override
    public void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId) {
        linksRepository.updateLinkInformationInDatabase(lastUpdated, lastChecked, urlId);
    }
}
