package edu.java.api.services.jpa;

import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.api.exceptions.LinkNotFoundException;
import edu.java.api.services.LinksService;
import edu.java.clients.holder.ClientsHolder;
import edu.java.domain.repository.jpa.JpaChatsRepository;
import edu.java.domain.repository.jpa.JpaLinksRepository;
import edu.java.domain.repository.jpa.entities.ChatEntity;
import edu.java.domain.repository.jpa.entities.LinkEntity;
import edu.java.models.Chat;
import edu.java.models.LinkData;
import edu.java.models.LinkDatabaseInformation;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class JpaLinksService implements LinksService {
    private final JpaLinksRepository linksRepository;
    private final JpaChatsRepository chatsRepository;
    private final ClientsHolder clientsHolder;

    @Override
    @SneakyThrows
    @Transactional
    public ListLinksResponse getTrackedLinks(long chatId) {
        ChatEntity chat = chatsRepository.findById(chatId).orElseThrow(() -> new ChatDoesNotExistException(chatId));
        Set<LinkEntity> linkEntitySet = chat.getLinks();
        return new ListLinksResponse(
            linkEntitySet.stream()
                .map(a ->
                    new LinkResponse(
                        a.getId(),
                        URI.create(a.getUri())
                    )
                )
                .toList()
        );
    }

    @Override
    @Transactional
    public LinkResponse addLinkToTracking(long chatId, AddLinkRequest addLinkRequest) {
        ChatEntity chat = chatsRepository.findById(chatId).orElseThrow(() -> new ChatDoesNotExistException(chatId));
        URI uri = addLinkRequest.link();
        Optional<LinkEntity> optionalLink = linksRepository.findByUri(uri.toString());
        LinkEntity link = optionalLink.orElseGet(() -> linksRepository.save(
                new LinkEntity(
                        uri.toString(),
                        getLastUpdatedTimeFromLink(uri),
                        OffsetDateTime.now()
                )
        ));
        chat.addLink(link);
        return new LinkResponse(
            link.getId(),
            uri
        );
    }

    @Override
    @Transactional
    public LinkResponse removeLinkFromTracking(long chatId, RemoveLinkRequest removeLinkRequest) {
        ChatEntity chat = chatsRepository.findById(chatId).orElseThrow(() -> new ChatDoesNotExistException(chatId));
        LinkEntity link = linksRepository.findById(removeLinkRequest.linkId()).orElseThrow(LinkNotFoundException::new);
        chat.deleteLink(link);
        if (link.getChats().isEmpty()) {
            linksRepository.delete(link);
        }
        return new LinkResponse(
            removeLinkRequest.linkId(),
            URI.create(link.getUri())
        );
    }

    @Override
    @Transactional
    public List<LinkDatabaseInformation> getAllLinksWhichWereNotCheckedForNminutes(int minutes) {
        return linksRepository.getAllByCheckedAtBeforeOrderByCheckedAtDesc(
            OffsetDateTime.now().minusMinutes(minutes)
        ).stream().map(entity ->
            new LinkDatabaseInformation(
                entity.getId(),
                URI.create(entity.getUri()),
                entity.getUpdatedAt()
            )
        ).toList();
    }

    @Override
    @Transactional
    public void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, OffsetDateTime lastChecked, long urlId) {
        LinkEntity link = linksRepository.findById(urlId).orElseThrow(RuntimeException::new);
        link.setUpdatedAt(lastUpdated);
        link.setCheckedAt(lastChecked);
        linksRepository.save(link);
    }

    @Override
    @Transactional
    public List<Chat> getChatsForLink(long urlId) {
        return linksRepository.findById(urlId).orElseThrow(RuntimeException::new).getChats().stream()
            .map(a -> new Chat(a.getId()))
            .toList();
    }

    private OffsetDateTime getLastUpdatedTimeFromLink(URI uri) {
        LinkData linkData = clientsHolder.checkURl(uri, null).get(0);
        if (linkData.url() != null && linkData.lastUpdated() != null) {
            return linkData.lastUpdated();
        }
        return null;
    }
}
