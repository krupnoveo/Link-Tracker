package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.response.ApiErrorResponse;
import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.clientService.DefaultBotService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

public class TrackCommandTest extends CommandTest {
    private Update update;
    private DefaultBotService defaultBotService;
    private Properties properties;

    @BeforeEach
    @SneakyThrows
    public void init() {
        update = Mockito.mock(Update.class);
        defaultBotService = Mockito.mock(DefaultBotService.class);
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        mockUpdate(update);
    }

    @Test
    @DisplayName("Тест TrackCommand.handleCommand(), когда команда /track и пользователь пытается добавить несколько ссылок")
    public void handleCommand_whenTryingToAddSeveralLinks_shouldReturnCorrectAnswer() {
        Mockito.when(update.message().text()).thenReturn(properties.getProperty("command.track.name") + " ya.ru google.com");

        TrackCommand trackCommand = new TrackCommand(properties, defaultBotService);

        SendMessage actual = trackCommand.handleCommand(update);

        SendMessage expected = new SendMessage(
            update.message().chat().id(),
            properties.getProperty("command.track.severalURLs")
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Тест TrackCommand.handleCommand(), когда команда /track и пользователь не указал ссылку после команду")
    public void handleCommand_whenMissingUrl_shouldReturnCorrectAnswer() {
        Mockito.when(update.message().text()).thenReturn(properties.getProperty("command.track.name"));

        TrackCommand trackCommand = new TrackCommand(properties, defaultBotService);

        SendMessage actual = trackCommand.handleCommand(update);

        SendMessage expected = new SendMessage(
            update.message().chat().id(),
            properties.getProperty("command.track.missingURL")
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Тест TrackCommand.handleCommand(), когда команда /track и добавление прошло успешно")
    public void handleCommand_whenAddLinkIsSuccessful_shouldReturnCorrectAnswer() {
        String url = "ya.ru";
        Mockito.when(update.message().text()).thenReturn(properties.getProperty("command.track.name") + " " + url);

        AddLinkToDatabaseResponse response = new AddLinkToDatabaseResponse(update.message().chat().id(), new URI(url));
        GenericResponse<AddLinkToDatabaseResponse> addResponse = new GenericResponse<>(response, null);
        Mockito.when(defaultBotService.addLinkToDatabase(url, update.message().chat().id())).thenReturn(addResponse);

        TrackCommand trackCommand = new TrackCommand(properties, defaultBotService);

        SendMessage actual = trackCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            update.message().chat().id(),
            properties.getProperty("command.track.addURL.success").formatted(url)
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Тест TrackCommand.handleCommand(), когда команда /track и добавление не удалось по какой-то причине")
    public void handleCommand_whenAddLinkIsFailed_shouldReturnCorrectAnswer() {
        String url = "ya.ru";
        String errorDescription = "не получилось";
        Mockito.when(update.message().text()).thenReturn(properties.getProperty("command.track.name") + " " + url);

        GenericResponse<AddLinkToDatabaseResponse> addResponse = new GenericResponse<>(null, new ApiErrorResponse(
            errorDescription,
            "",
            "",
            "",
            List.of()
        ));
        Mockito.when(defaultBotService.addLinkToDatabase(url, update.message().chat().id())).thenReturn(addResponse);

        TrackCommand trackCommand = new TrackCommand(properties, defaultBotService);

        SendMessage actual = trackCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            update.message().chat().id(),
            properties.getProperty("command.track.addURL.fail").formatted(errorDescription)
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }
}
