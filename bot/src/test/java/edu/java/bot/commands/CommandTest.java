package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CommandTest {
    @Mock
    private Message message;
    @Mock
    private Chat chat;
    protected void mockUpdate(Update update) {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(update.message().chat()).thenReturn(chat);
        Mockito.when(update.message().chat().id()).thenReturn(1L);
    }
}
