package edu.java.bot.api.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.request.LinkUpdate;
import edu.java.bot.printerToChat.ChatResponser;
import edu.java.bot.printerToChat.DefaultChatResponser;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

public class LinkUpdatesServiceTest {
    @Test
    @SneakyThrows
    public void notifyUsers_shouldWorkCorrectly() {
        ChatResponser responser = Mockito.mock(DefaultChatResponser.class);
        List<LinkUpdate> linkUpdateList = List.of(
            new LinkUpdate(1L, new URI("https://ya.ru"), "По ссылке %s произошло обновление!", List.of(1L)),
            new LinkUpdate(2L, new URI("https://google.ru"), "По ссылке %s произошло обновление!", List.of(2L))
        );
        LinkUpdatesService linkUpdatesService = new LinkUpdatesService(responser);
        linkUpdatesService.notifyUsers(linkUpdateList);
        SendMessage expected1 = new SendMessage(
            1L,
            "По ссылке https://ya.ru произошло обновление!"
        );
        SendMessage expected2 = new SendMessage(
            2L,
            "По ссылке https://google.ru произошло обновление!"
        );
        Mockito.verify(responser).sendMessage(Mockito.argThat(new SendMessageMatcher(expected1)));
        Mockito.verify(responser).sendMessage(Mockito.argThat(new SendMessageMatcher(expected2)));
    }

    private record SendMessageMatcher(SendMessage expected) implements ArgumentMatcher<SendMessage> {

        @Override
        public boolean matches(SendMessage sendMessage) {
            return sendMessage.getParameters().get("text").equals(expected.getParameters().get("text"))
                && sendMessage.getParameters().get("chat_id").equals(expected.getParameters().get("chat_id"));
        }
    }
}
