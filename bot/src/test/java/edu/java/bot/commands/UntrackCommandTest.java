package edu.java.bot.commands;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.response.ApiErrorResponse;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.Link;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.clientService.DefaultBotService;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UntrackCommandTest extends CommandTest {
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
    }
    @SneakyThrows
    @Test
    @DisplayName("Тест UntrackCommand.handleCommand(), когда пришло сообщение и в данный момент нет отслеживаемых ссылок")
    public void handleCommand_whenUpdateIsMessage_and_noTrackedLinks_shouldReturnCorrectAnswer() {
        mockUpdate(update);
        ListLinksResponse linksResponse = new ListLinksResponse(List.of());
        GenericResponse<ListLinksResponse> response = new GenericResponse<>(linksResponse, null);
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);
        SendMessage actual = untrackCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            update.message().chat().id(),
            properties.getProperty("command.untrack.empty")
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Тест UntrackCommand.handleCommand(), когда пришло сообщение и в данный момент есть отслеживаемые ссылки")
    public void handleCommand_whenUpdateIsMessage_and_areTrackedLinks_shouldReturnCorrectAnswer() {
        mockUpdate(update);
        ListLinksResponse linksResponse = new ListLinksResponse(List.of(new Link(1L, new URI("ya.ru"))));
        GenericResponse<ListLinksResponse> response = new GenericResponse<>(linksResponse, null);
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);
        SendMessage actual = untrackCommand.handleCommand(update);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : response.response().links()) {
            keyboardMarkup.addRow(
                new InlineKeyboardButton(link.url().toString())
                    .callbackData(
                        properties.getProperty("command.untrack.name") + properties.getProperty("command.untrack.callBackDelimiter") + link.id()
                    )
            );
        }
        SendMessage expected = new SendMessage(
            update.message().chat().id(),
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
    @Test
    @DisplayName("Тест UntrackCommand.handleCommand(), когда пришло сообщение и не удалось вывести список отслеживаемых ссылок")
    public void handleCommand_whenUpdateIsMessage_and_errorWhenListingLinks_shouldReturnCorrectAnswer() {
        mockUpdate(update);
        String errorDescription = "не получилось";
        GenericResponse<ListLinksResponse> response = new GenericResponse<>(null, new ApiErrorResponse(
            errorDescription,
            "",
            "",
            "",
            List.of()
        ));
        Mockito.when(botService.listLinksFromDatabase(update.message().chat().id())).thenReturn(response);

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);
        SendMessage actual = untrackCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            update.message().chat().id(),
            properties.getProperty("command.untrack.handleCommand.error").formatted(errorDescription)
        );
        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Тест UntrackCommand.handleCommand(), когда пришел колбэк и удаление ссылки прошло успешно")
    public void handleCommand_whenUpdateIsCallBack_and_UrlRemovedSuccessfully_shouldReturnCorrectAnswer() {
        String url = "ya.ru";
        CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class);
        User user = Mockito.mock(User.class);
        long linkId = 1L;

        Mockito.when(update.callbackQuery()).thenReturn(callbackQuery);
        Mockito.when(update.callbackQuery().from()).thenReturn(user);
        Mockito.when(update.callbackQuery().from().id()).thenReturn(1L);
        Mockito.when(update.callbackQuery().data()).thenReturn(
            properties.getProperty("command.untrack.name")
                + properties.getProperty("command.untrack.callBackDelimiter")
                + linkId
        );

        RemoveLinkFromDatabaseResponse removeResponse = new RemoveLinkFromDatabaseResponse(linkId, new URI(url));
        GenericResponse<RemoveLinkFromDatabaseResponse> response = new GenericResponse<>(removeResponse, null);
        Mockito.when(botService.removeLinkFromDatabase(linkId, update.callbackQuery().from().id())).thenReturn(response);

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);

        SendMessage actual = untrackCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.untrack.removeURL.success").formatted(url)
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Тест UntrackCommand.handleCommand(), когда пришел колбэк и удаление ссылки не удалось")
    public void handleCommand_whenUpdateIsCallBack_and_UrlRemovalFailed_shouldReturnCorrectAnswer() {
        long linkId = 1L;
        String errorDescription = "не получилось";
        CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class);
        User user = Mockito.mock(User.class);

        Mockito.when(update.callbackQuery()).thenReturn(callbackQuery);
        Mockito.when(update.callbackQuery().from()).thenReturn(user);
        Mockito.when(update.callbackQuery().from().id()).thenReturn(1L);
        Mockito.when(update.callbackQuery().data()).thenReturn(
            properties.getProperty("command.untrack.name")
                + properties.getProperty("command.untrack.callBackDelimiter")
                + linkId
        );

        GenericResponse<RemoveLinkFromDatabaseResponse> response = new GenericResponse<>(null, new ApiErrorResponse(
            errorDescription,
            "",
            "",
            "",
            List.of()
        ));
        Mockito.when(botService.removeLinkFromDatabase(linkId, update.callbackQuery().from().id())).thenReturn(response);

        UntrackCommand untrackCommand = new UntrackCommand(properties, botService);

        SendMessage actual = untrackCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.untrack.removeURL.fail").formatted(errorDescription)
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @Test
    @DisplayName("Тест UntrackCommand.isSupportsUpdate(), когда пришли подходящие данные")
    public void isSupportsUpdate_whenUpdateHaveNecessaryData_shouldReturnTrue() {
        CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class);
        CommandHandler handler = new UntrackCommand(properties, botService);

        Mockito.when(update.callbackQuery()).thenReturn(callbackQuery);
        Mockito.when(update.callbackQuery().data()).thenReturn("some text");

        assertThat(handler.isSupportsUpdate(update)).isTrue();
    }

    @Test
    @DisplayName("Тест UntrackCommand.isSupportsUpdate(), когда пришли неподходящие данные")
    public void isSupportsUpdate_whenUpdateNotHaveNecessaryData_shouldReturnFalse() {
        CallbackQuery callbackQuery = Mockito.mock(CallbackQuery.class);
        CommandHandler handler = new UntrackCommand(properties, botService);

        Mockito.when(update.callbackQuery()).thenReturn(callbackQuery);

        assertThat(handler.isSupportsUpdate(update)).isFalse();
    }
}
