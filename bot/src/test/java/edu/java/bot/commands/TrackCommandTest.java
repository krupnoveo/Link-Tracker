package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.service.DefaultBotService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest extends CommandTest {

    @DisplayName("Тест TrackCommand.handleCommand(), когда команда равна /track и добавление ссылки (ссылок) успешно")
    @Test
    public void handleCommand_whenCommandIsTrack_and_linkAdditionSucceed() {
        testWhenCommandIsTrack(
            true,
            null,
            "command.track.addURL.success",
            "test1.ru test2.ru"
        );
    }


    @Test
    public void handleCommand_whenCommandIsTrack_and_linkAdditionFailed() {
        testWhenCommandIsTrack(
            false,
            "эти ссылки уже были добавлены",
            "command.track.addURL.fail",
            "эти ссылки уже были добавлены"
        );
    }

    @SneakyThrows
    private void testWhenCommandIsTrack(boolean status, String responseFromDatabase, String messageResponse, String formattedPart) {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        String url = "test1.ru test2.ru";

        mockUpdate(update, properties.getProperty("command.track.name") + " " + url);

        AddLinkToDatabaseResponse response = new AddLinkToDatabaseResponse(
            status,
            responseFromDatabase
        );

        Mockito.when(botService.addLinkToDatabase(url, update.message().chat().id())).thenReturn(response);

        TrackCommand command = new TrackCommand(properties, botService);

        SendMessage actual = command.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty(messageResponse).formatted(formattedPart)
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @Test
    public void handleCommand_whenCommandIsTrack_and_noUrlWasEntered() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        mockUpdate(update, properties.getProperty("command.track.name"));

        TrackCommand command = new TrackCommand(properties, botService);

        SendMessage actual = command.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.track.missingURL")
        );

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @Test
    public void handleCommand_whenCommandIsNotTrack() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        TrackCommand trackCommand = new TrackCommand(properties, botService);
        testWhenTransmittedCommandIsNotEqualToCurrentClassCommand(trackCommand, update, properties);
    }
}
