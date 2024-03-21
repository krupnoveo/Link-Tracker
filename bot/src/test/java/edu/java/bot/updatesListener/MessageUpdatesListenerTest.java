package edu.java.bot.updatesListener;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.commandsHolder.CommandsHolder;
import edu.java.bot.printerToChat.DefaultChatResponser;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MessageUpdatesListenerTest {
    @Test
    @SneakyThrows
    @DisplayName("Тест MessagesUpdatesListener.process(), когда пришел какой-то текст вместо команды")
    public void process_whenUpdateHasSomeRandomText_shouldReturnCommandUnknownMessage() {
        long chatId = 1L;
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        CommandsHolder holder = Mockito.mock(CommandsHolder.class);
        StartCommand command = Mockito.mock(StartCommand.class);
        DefaultChatResponser responser = Mockito.mock(DefaultChatResponser.class);
        Update update1 = Mockito.mock(Update.class);
        Update update2 = Mockito.mock(Update.class);
        Update update3 = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);
        CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class);
        User user = Mockito.mock(User.class);

        Mockito.when(update1.message()).thenReturn(message);
        Mockito.when(update1.message().chat()).thenReturn(chat);
        Mockito.when(update1.message().chat().id()).thenReturn(chatId);

        Mockito.when(update2.callbackQuery()).thenReturn(callbackQuery);
        Mockito.when(update2.callbackQuery().from()).thenReturn(user);
        Mockito.when(update2.callbackQuery().from().id()).thenReturn(chatId);

        Mockito.when(update3.editedMessage()).thenReturn(message);
        Mockito.when(update3.editedMessage().chat()).thenReturn(chat);
        Mockito.when(update3.editedMessage().chat().id()).thenReturn(chatId);

        Mockito.when(holder.getCommandHandlers()).thenReturn(Map.of(
            properties.getProperty("command.start.name"), command
        ));
        UpdatesListener listener = new MessageUpdatesListener(responser, holder, properties);

        SendMessage expected = new SendMessage(
            chatId,
            properties.getProperty("command.unknown")
        );
        listener.process(List.of(update1, update2, update3));
        Mockito.verify(responser, Mockito.times(3)).sendMessage(Mockito.argThat(new SendMessageMatcher(expected)));
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест MessagesUpdatesListener.process(), когда пришла команда")
    public void process_whenUpdateCorrectCommand_shouldReturnCorrectMessage() {
        long chatId = 1L;
        String url = "ya.ru";
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        CommandsHolder holder = Mockito.mock(CommandsHolder.class);
        StartCommand startCommand = Mockito.mock(StartCommand.class);
        UntrackCommand untrackCommand = Mockito.mock(UntrackCommand.class);
        DefaultChatResponser responser = Mockito.mock(DefaultChatResponser.class);
        Update update1 = Mockito.mock(Update.class);
        Update update2 = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class);

        Mockito.when(startCommand.handleCommand(update1)).thenReturn(new SendMessage(
            chatId,
            properties.getProperty("command.start.hello")
        ));
        Mockito.when(untrackCommand.handleCommand(update2)).thenReturn(new SendMessage(
            chatId,
            properties.getProperty("command.untrack.removeURL.success").formatted(url)
        ));
        Mockito.when(startCommand.isSupportsUpdate(update1)).thenReturn(true);
        Mockito.when(untrackCommand.isSupportsUpdate(update2)).thenReturn(true);
        Mockito.when(update1.message()).thenReturn(message);
        Mockito.when(update1.message().text()).thenReturn("   " + properties.getProperty("command.start.name") + "  ");
        Mockito.when(update2.callbackQuery()).thenReturn(callbackQuery);
        Mockito.when(update2.callbackQuery().data()).thenReturn(
            properties.getProperty("command.untrack.name")
                + properties.getProperty("command.untrack.callBackDelimiter")
                + 1L
            );
        Mockito.when(holder.getCommandHandlers()).thenReturn(Map.of(
            properties.getProperty("command.start.name"), startCommand,
            properties.getProperty("command.untrack.name"), untrackCommand
        ));
        UpdatesListener listener = new MessageUpdatesListener(responser, holder, properties);

        SendMessage expectedFromStart = new SendMessage(
            chatId,
            properties.getProperty("command.start.hello")
        );
        SendMessage expectedFromUntrack = new SendMessage(
            chatId,
            properties.getProperty("command.untrack.removeURL.success").formatted(url)
        );
        listener.process(List.of(update1, update2));
        Mockito.verify(responser).sendMessage(Mockito.argThat(new SendMessageMatcher(expectedFromStart)));
        Mockito.verify(responser).sendMessage(Mockito.argThat(new SendMessageMatcher(expectedFromUntrack)));
    }

    private record SendMessageMatcher(SendMessage expected) implements ArgumentMatcher<SendMessage> {

        @Override
            public boolean matches(SendMessage sendMessage) {
                return sendMessage.getParameters().get("text").equals(expected.getParameters().get("text"))
                    && sendMessage.getParameters().get("chat_id").equals(expected.getParameters().get("chat_id"));
            }
        }
}
