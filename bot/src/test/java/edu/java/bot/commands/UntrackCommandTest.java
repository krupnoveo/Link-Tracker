package edu.java.bot.commands;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.Link;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.service.DefaultBotService;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UntrackCommandTest extends CommandTest {

    @SneakyThrows
    @DisplayName("Тест UntrackCommand.handleCommand(), когда команда равна /untrack, и колбэк пустой, и список отслеживаемых ссылок пуст")
    @Test
    public void handleCommand_whenCommandIsUntrack_and_callbackIsNull_and_linksListIsEmpty() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        ListLinksResponse response = new ListLinksResponse(
            List.of()
        );

        mockUpdate(update, properties.getProperty("command.untrack.name"));
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);

        SendMessage actual = untrackCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.untrack.empty")
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @DisplayName("Тест UntrackCommand.handleCommand(), когда команда равна /untrack, и колбэк пустой, и список отслеживаемых ссылок НЕ пуст")
    @Test
    public void handleCommand_whenCommandIsUntrack_and_callbackIsNull_and_linksListIsNotEmpty() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        ListLinksResponse response = new ListLinksResponse(
            List.of(new Link("test.ru", UUID.randomUUID()))
        );

        mockUpdate(update, properties.getProperty("command.untrack.name"));
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);

        SendMessage actual = untrackCommand.handleCommand(update);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : response.links()) {
            keyboardMarkup.addRow(
                new InlineKeyboardButton(link.url())
                    .callbackData(
                        properties.getProperty("command.untrack.name") + ":" + link.uuid()
                    )
            );
        }
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.untrack.chooseLinkToRemove")
        ).replyMarkup(keyboardMarkup);

        InlineKeyboardButton actualParameter = ((InlineKeyboardButton[]) ((InlineKeyboardMarkup) actual.getParameters().get("reply_markup")).inlineKeyboard()[0])[0];
        InlineKeyboardButton expectedParameter = ((InlineKeyboardButton[]) ((InlineKeyboardMarkup) expected.getParameters().get("reply_markup")).inlineKeyboard()[0])[0];


        assertThat(actualParameter.text()).isEqualTo(expectedParameter.text());
        assertThat(actualParameter.callbackData()).isEqualTo(expectedParameter.callbackData());

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @DisplayName("Тест UntrackCommand.handleCommand(), когда команда равна /untrack, и колбэк НЕ пустой, и ссылка была успешно удалена")
    @Test
    public void handleCommand_whenCommandIsUntrack_and_callbackIsNotNull_and_linkRemovalSucceed() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);
        CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class, Answers.CALLS_REAL_METHODS);
        User user = Mockito.mock(User.class, Answers.CALLS_REAL_METHODS);
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        Mockito.when(update.callbackQuery()).thenReturn(callbackQuery);
        Mockito.when(update.callbackQuery().from()).thenReturn(user);
        Mockito.when(update.callbackQuery().from().id()).thenReturn(1L);
        UUID uuid = UUID.randomUUID();
        Mockito.when(update.callbackQuery().data()).thenReturn(
            properties.getProperty("command.untrack.name") + ":" + uuid
        );

        RemoveLinkFromDatabaseResponse response = new RemoveLinkFromDatabaseResponse(
            true,
            null,
            new Link("test.ru", uuid)
        );

        Mockito.when(botService.removeLinkFromDatabase(uuid, 1L)).thenReturn(response);

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);

        SendMessage actual = untrackCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.untrack.removeURL.success").formatted("test.ru")
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @DisplayName("Тест UntrackCommand.handleCommand(), когда команда равна /untrack, и колбэк НЕ пустой, и ссылка НЕ была успешно удалена")
    @Test
    public void handleCommand_whenCommandIsUntrack_and_callbackIsNotNull_and_linkRemovalFailed() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);
        CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class, Answers.CALLS_REAL_METHODS);
        User user = Mockito.mock(User.class, Answers.CALLS_REAL_METHODS);
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        Mockito.when(update.callbackQuery()).thenReturn(callbackQuery);
        Mockito.when(update.callbackQuery().from()).thenReturn(user);
        Mockito.when(update.callbackQuery().from().id()).thenReturn(1L);
        UUID uuid = UUID.randomUUID();
        Mockito.when(update.callbackQuery().data()).thenReturn(
            properties.getProperty("command.untrack.name") + ":" + uuid
        );

        RemoveLinkFromDatabaseResponse response = new RemoveLinkFromDatabaseResponse(
            false,
            "такой ссылки нет среди отслеживаемых",
            null
        );

        Mockito.when(botService.removeLinkFromDatabase(uuid, 1L)).thenReturn(response);

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);

        SendMessage actual = untrackCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.untrack.removeURL.fail").formatted("такой ссылки нет среди отслеживаемых")
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @DisplayName("Тест UntrackCommand.handleCommand(), когда команда НЕ равна /untrack")
    @Test
    public void handleCommand_whenCommandIsNotUntrack() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);

        testWhenTransmittedCommandIsNotEqualToCurrentClassCommand(untrackCommand, update, properties);
    }
}
