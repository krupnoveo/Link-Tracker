package edu.java.api.services.jdbc;

import edu.java.api.dto.response.LinkResponse;
import edu.java.api.services.ChatService;
import edu.java.domain.repository.JdbcChatRepository;
import edu.java.domain.repository.JdbcChatsToLinksRepository;
import edu.java.domain.repository.JdbcLinksRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {
    private final JdbcChatRepository jdbcChatRepository;
    private final JdbcLinksRepository jdbcLinksRepository;
    private final JdbcChatsToLinksRepository jdbcChatsToLinksRepository;

    public void registerChat(long chatId) {
        log.info("Registering chat...");
        jdbcChatRepository.add(chatId);
    }

    public void deleteChat(long chatId) {
        log.info("Deleting chat...");
        List<LinkResponse> notTrackedByAnyoneLinks = jdbcChatsToLinksRepository.removeChatAndAllConnectedLinks(chatId);
        jdbcChatRepository.remove(chatId);
        jdbcLinksRepository.removeAll(notTrackedByAnyoneLinks);
    }
}
