package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.response.ApiErrorResponse;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.Link;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.service.DefaultBotService;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;


public class ListCommandTest extends CommandTest {
    private Update update;
    private DefaultBotService botService;
    private Properties properties;

    @BeforeEach
    @SneakyThrows
    public void init() {
        update = Mockito.mock(Update.class);
        botService = Mockito.mock(DefaultBotService.class);
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        mockUpdate(update);
    }


    @SneakyThrows
    @DisplayName("Тест ListCommand.handleCommand(), когда команда равна /list и список ссылок не пустой")
    @Test
    public void handleCommand_whenCommandIsList_and_linksListIsNotEmpty_shouldReturnCorrectMessage() {
        ListLinksResponse linksResponse = new ListLinksResponse(List.of(new Link(1L, new URI("ya.ru"))));

        GenericResponse<ListLinksResponse> response = new GenericResponse<>(linksResponse, null);
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        ListCommand listCommand = new ListCommand(properties, botService);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : response.response().links()) {
            keyboardMarkup.addRow(new InlineKeyboardButton(link.url().toString()).url(link.url().toString()));
        }

        SendMessage actual = listCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            update.message().chat().id(),
            properties.getProperty("command.list.listLinks.success")).replyMarkup(keyboardMarkup);

        InlineKeyboardButton actualParameter = ((InlineKeyboardButton[]) ((InlineKeyboardMarkup) actual.getParameters().get("reply_markup")).inlineKeyboard()[0])[0];
        InlineKeyboardButton expectedParameter = ((InlineKeyboardButton[]) ((InlineKeyboardMarkup) expected.getParameters().get("reply_markup")).inlineKeyboard()[0])[0];

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));

        assertThat(actualParameter.text()).isEqualTo(expectedParameter.text());
        assertThat(actualParameter.url()).isEqualTo(expectedParameter.url());
    }

    @DisplayName("Тест ListCommand.handleCommand(), когда команда равна /list и список ссылок пустой")
    @Test
    public void handleCommand_whenCommandIsList_and_linksListIsEmpty_shouldReturnCorrectMessage() {
        ListLinksResponse linksResponse = new ListLinksResponse(List.of());
        GenericResponse<ListLinksResponse> response = new GenericResponse<>(linksResponse, null);
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        ListCommand listCommand = new ListCommand(properties, botService);

        SendMessage actual = listCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            update.message().chat().id(),
            properties.getProperty("command.list.empty"));

        Object actualParameter = actual.getParameters().get("reply_markup");
        Object expectedParameter = expected.getParameters().get("reply_markup");

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));

        assertThat(actualParameter).isEqualTo(expectedParameter).isNull();
    }
    @SneakyThrows
    @Test
    @DisplayName("Тест ListCommand.handleCommand(), когда команда /list и не удалось получить список ссылок")
    public void handleCommand_whenCommandIsList_and_failedToGetListOfLinks() {
        String errorDescription = "не получилось";
        GenericResponse<ListLinksResponse> response = new GenericResponse<>(null, new ApiErrorResponse(
            errorDescription,
            "",
            "",
            "",
            List.of()
        ));
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        ListCommand listCommand = new ListCommand(properties, botService);

        SendMessage actual = listCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            update.message().chat().id(),
            properties.getProperty("command.list.listLinks.fail").formatted(errorDescription)
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }
}
