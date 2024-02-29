package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
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
public class StartCommandTest extends CommandTest {

    @SneakyThrows
    @DisplayName("Тест StartCommand.handleCommand(), когда команда равна /start и пользователь успешно зарегистрирован")
    @Test
    public void handleCommand_whenCommandIsStart_and_successfulRegistration_shouldReturnCorrectMessage() {
        test(true, "command.start.hello");
    }

    @DisplayName("Тест StartCommand.handleCommand(), когда команда равна /start и пользователь не смог быть зарегистрирован")
    @Test
    public void handleCommand_whenCommandIsStart_and_failedRegistration_shouldReturnCorrectMessage() {
        test(false, "command.start.failedRegistration");
    }

    @SneakyThrows
    private void test(boolean isSuccessful, String expectedMessage) {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        mockUpdate(update, properties.getProperty("command.start.name"));
        Mockito.when(update.message().chat().firstName()).thenReturn("name");
        Mockito.when(botService.registerUser(
                new User(1L))
            ).thenReturn(isSuccessful);

        StartCommand command = new StartCommand(properties, botService);

        SendMessage actual = command.handleCommand(update);
        SendMessage expected = new SendMessage(1L, properties.getProperty(expectedMessage));

        assertThat(actual.getParameters().get("text"))
            .isEqualTo(expected.getParameters().get("text"));
        assertThat(expected.getParameters().get("chat_id"))
            .isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @DisplayName("Тест StartCommand.handleCommand(), когда команда не равна /start и отсутствует следующее звено цепи")
    @Test
    public void handleCommand_whenCommandIsNotStart_shouldReturnCorrectMessage() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        DefaultBotService botService = Mockito.mock(DefaultBotService.class, Answers.CALLS_REAL_METHODS);
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        StartCommand startCommand = new StartCommand(properties, botService);

        testWhenTransmittedCommandIsNotEqualToCurrentClassCommand(startCommand, update, properties);
    }
}
