package edu.java.scrapper.api.services.jdbc;

import edu.java.api.services.jdbc.JdbcChatService;
import edu.java.domain.ChatsRepository;
import edu.java.domain.ChatsToLinksRepository;
import edu.java.domain.LinksRepository;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JdbcChatServiceTest {
    @Mock
    private ChatsRepository chatsRepository;
    @Mock
    private LinksRepository linksRepository;
    @Mock
    private ChatsToLinksRepository chatsToLinksRepository;
    @InjectMocks
    private JdbcChatService chatService;

    @Test
    public void registerChat_shouldWorkCorrectly() {
        chatService.registerChat(1L);
        Mockito.verify(chatsRepository).add(1L);
    }

    @Test
    @SneakyThrows
    public void deleteChat_shouldWorkCorrectly() {
        chatService.deleteChat(1L);
        Mockito.verify(chatsToLinksRepository).removeChatAndAllConnectedLinks(1L);
        Mockito.verify(chatsRepository).remove(1L);
        Mockito.verify(linksRepository).removeAll(List.of());
    }
}
