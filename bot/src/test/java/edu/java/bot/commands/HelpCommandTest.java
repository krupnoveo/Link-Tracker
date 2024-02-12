package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commandsHolder.CommandsHolder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest extends CommandTest {
    @Mock
    private CommandsHolder commandsHolder;

    @SneakyThrows
    @DisplayName("Тест HelpCommand.handleCommand(), когда команда равна /help")
    @Test
    public void handleCommand_whenCommandIsHelp_shouldReturnCorrectMessage() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));

        String commandNameForTest = properties.getProperty("command.start.name");
        String commandDescriptionForTest = properties.getProperty("command.start.description");

        Map<String, String> commands = new LinkedHashMap<>();
        commands.put(
            commandNameForTest,
            commandDescriptionForTest
        );

        Mockito.when(commandsHolder.getCommandsNameAndDescriptions()).thenReturn(commands);
        mockUpdate(update, properties.getProperty("command.help.name"));

        HelpCommand helpCommand = new HelpCommand(properties, commandsHolder);
        SendMessage actual = helpCommand.handleCommand(update);
        SendMessage expected = new SendMessage(
            1L,
            properties.getProperty("command.help.listCommands") + "\n" +
                commandNameForTest + " - " + commandDescriptionForTest.toLowerCase());

        assertThat(actual.getParameters().get("text")).isEqualTo(expected.getParameters().get("text"));
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(expected.getParameters().get("chat_id"));
    }

    @SneakyThrows
    @DisplayName("Тест HelpCommand.handleCommand(), когда команда не равна /help, а следующее звено цепи отсутствует")
    @Test
    public void handleCommand_whenCommandIsNotHelp_shouldReturnCorrectMessage() {
        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        HelpCommand helpCommand = new HelpCommand(properties, commandsHolder);

        testWhenTransmittedCommandIsNotEqualToCurrentClassCommand(helpCommand, update, properties);
    }
}
