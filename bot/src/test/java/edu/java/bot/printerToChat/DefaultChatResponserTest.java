package edu.java.bot.printerToChat;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

public class DefaultChatResponserTest {
    @Test
    @DisplayName("Тест DefaultChatResponser.sendMessage()")
    public void sendMessage_shouldWorkCorrectly() {
        TelegramBot bot = Mockito.mock(TelegramBot.class, Answers.RETURNS_MOCKS);
        SendMessage message = new SendMessage(1L, "");
        Mockito.when(bot.execute(Mockito.any())).thenReturn(null);
        ChatResponser responser = new DefaultChatResponser(bot);

        responser.sendMessage(message);

        Mockito.verify(bot).execute(message);
    }
}
