package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.response.ApiErrorResponse;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.User;
import edu.java.bot.clientService.DefaultBotService;
import java.util.List;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

public class StartCommandTest extends CommandTest {

    @SneakyThrows
    @DisplayName("Тест StartCommand.handleCommand(), когда команда равна /start и пользователь успешно зарегистрирован")
    @Test
    public void handleCommand_whenCommandIsStart_and_successfulRegistration_shouldReturnCorrectMessage() {
        test(true, "command.start.hello", "");
    }

    @DisplayName("Тест StartCommand.handleCommand(), когда команда равна /start и пользователь не смог быть зарегистрирован")
    @Test
    public void handleCommand_whenCommandIsStart_and_failedRegistration_shouldReturnCorrectMessage() {
        test(false, "command.start.failedRegistration", "не получилось");
    }

    @SneakyThrows
    private void test(boolean isSuccessful, String expectedMessage, String errorDescription) {
        Update update = Mockito.mock(Update.class);

        DefaultBotService botService = Mockito.mock(DefaultBotService.class);

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        mockUpdate(update);

        GenericResponse<Void> fail = new GenericResponse<>(null, new ApiErrorResponse(
            errorDescription,
            "",
            "",
            "",
            List.of()
        ));
        GenericResponse<Void> success = new GenericResponse<>(null, null);
        if (isSuccessful) {
            Mockito.when(botService.registerUser(
                new User(update.message().chat().id()))
            ).thenReturn(success);
        } else {
            Mockito.when(botService.registerUser(
                new User(update.message().chat().id()))
            ).thenReturn(fail);
        }

        StartCommand command = new StartCommand(properties, botService);

        SendMessage actual = command.handleCommand(update);
        SendMessage expected = new SendMessage(update.message().chat().id(), properties.getProperty(expectedMessage).formatted(errorDescription));

        assertThat(actual.getParameters().get("text"))
            .isEqualTo(expected.getParameters().get("text"));
        assertThat(expected.getParameters().get("chat_id"))
            .isEqualTo(expected.getParameters().get("chat_id"));
    }
}
