package edu.java.api.services.jdbc;

import edu.java.api.dto.response.LinkResponse;
import edu.java.api.services.ChatService;
import edu.java.domain.ChatsRepository;
import edu.java.domain.ChatsToLinksRepository;
import edu.java.domain.LinksRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@ConditionalOnProperty(name = "database.access-via", havingValue = "jdbc")
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {
    private final ChatsRepository chatRepository;
    private final LinksRepository linksRepository;
    private final ChatsToLinksRepository chatsToLinksRepository;

    public void registerChat(long chatId) {
        log.info("Registering chat...");
        chatRepository.add(chatId);
    }

    public void deleteChat(long chatId) {
        log.info("Deleting chat...");
        List<LinkResponse> notTrackedByAnyoneLinks = chatsToLinksRepository.removeChatAndAllConnectedLinks(chatId);
        chatRepository.remove(chatId);
        linksRepository.removeAll(notTrackedByAnyoneLinks);
    }
}
