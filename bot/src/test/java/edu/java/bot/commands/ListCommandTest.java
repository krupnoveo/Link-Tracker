package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.Link;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.service.DefaultBotService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest extends CommandTest {

    @SneakyThrows
    @DisplayName("Тест ListCommand.handleCommand(), когда команда равна /list и список ссылок не пустой")
    @Test
    public void handleCommand_whenCommandIsList_and_linksListIsNotEmpty_shouldReturnCorrectMessage() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        mockUpdate(update, properties.getProperty("command.list.name"));

        ListLinksResponse response = new ListLinksResponse(List.of(new Link("test.ru", UUID.randomUUID())));
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        ListCommand listCommand = new ListCommand(properties, botService);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : response.links()) {
            keyboardMarkup.addRow(new InlineKeyboardButton(link.url()).url(link.url()));
        }

        SendMessage actual = listCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.list.notEmpty")).replyMarkup(keyboardMarkup);

        InlineKeyboardButton actualParameter = ((InlineKeyboardButton[]) ((InlineKeyboardMarkup) actual.getParameters().get("reply_markup")).inlineKeyboard()[0])[0];
        InlineKeyboardButton expectedParameter = ((InlineKeyboardButton[]) ((InlineKeyboardMarkup) expected.getParameters().get("reply_markup")).inlineKeyboard()[0])[0];

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));

        assertThat(actualParameter.text()).isEqualTo(expectedParameter.text());
        assertThat(actualParameter.url()).isEqualTo(expectedParameter.url());
    }

    @SneakyThrows
    @DisplayName("Тест ListCommand.handleCommand(), когда команда равна /list и список ссылок пустой")
    @Test
    public void handleCommand_whenCommandIsList_and_linksListIsEmpty_shouldReturnCorrectMessage() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        mockUpdate(update, properties.getProperty("command.list.name"));

        ListLinksResponse response = new ListLinksResponse(List.of());
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        ListCommand listCommand = new ListCommand(properties, botService);

        SendMessage actual = listCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.list.empty"));

        Object actualParameter = actual.getParameters().get("reply_markup");
        Object expectedParameter = expected.getParameters().get("reply_markup");

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));

        assertThat(actualParameter).isEqualTo(expectedParameter).isNull();
    }

    @SneakyThrows
    @DisplayName("Тест ListCommand.handleCommand(), когда команда не равна /list")
    @Test
    public void handleCommand_whenCommandIsNotList_shouldReturnCorrectAnswer() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        ListCommand listCommand = new ListCommand(properties, botService);

        testWhenTransmittedCommandIsNotEqualToCurrentClassCommand(listCommand, update, properties);
    }
}
