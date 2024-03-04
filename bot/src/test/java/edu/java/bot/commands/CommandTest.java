package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandTest {
    @Mock
    private Message message;
    @Mock
    private Chat chat;
    protected void mockUpdate(Update update, String text) {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(update.message().chat()).thenReturn(chat);
        Mockito.when(update.message().text()).thenReturn(text);
        Mockito.when(update.message().chat().id()).thenReturn(1L);
    }

    protected void testWhenTransmittedCommandIsNotEqualToCurrentClassCommand(
        CommandHandler testedClass,
        Update update,
        Properties properties
    ) {
        mockUpdate(update, "/unknown");
        SendMessage actual = testedClass.handleCommand(update);
        SendMessage expected = new SendMessage(1L, properties.getProperty("command.unknown"));

        assertThat(actual.getParameters().get("text"))
            .isEqualTo(expected.getParameters().get("text"));
        assertThat(expected.getParameters().get("chat_id"))
            .isEqualTo(expected.getParameters().get("chat_id"));
    }
}
