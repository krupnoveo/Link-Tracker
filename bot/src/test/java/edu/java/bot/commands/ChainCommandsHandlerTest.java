package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commandsHolder.CommandsHolder;
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
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ChainCommandsHandlerTest extends CommandTest {

    private Properties properties;
    private CommandsHolder commandsHolder;
    private ChainCommandsHandler chainCommandsHandler;
    private List<CommandHandler> commands;

    @SneakyThrows
    @DisplayName("Тест ChainCommandsHandler.setUpChain()")
    @Test
    public void setUpChain_shouldWorkCorrectly() {
        setUpTest();

        assertThat(commands.get(0).nextHandler).isInstanceOf(ListCommand.class);
        assertThat(commands.get(1).nextHandler).isInstanceOf(TrackCommand.class);
    }

    @SneakyThrows
    @DisplayName("Тест ChainCommandsHandler.handleCommand()")
    @Test
    public void handleCommand_shouldReturnUnknownCommandMessage() {
        setUpTest();

        Update update = Mockito.mock(Update.class, Answers.CALLS_REAL_METHODS);
        mockUpdate(update, "/unknown");
        SendMessage message = chainCommandsHandler.handleCommand(update);
        assertThat(message.getParameters().get("text")).isEqualTo(properties.getProperty("command.unknown"));
    }

    @SneakyThrows
    private void setUpTest() {
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        commandsHolder = Mockito.mock(CommandsHolder.class, Answers.CALLS_REAL_METHODS);
        commands = List.of(
            new StartCommand(properties, new DefaultBotService()),
            new ListCommand(properties, new DefaultBotService()),
            new TrackCommand(properties, new DefaultBotService()),
            new HelpCommand(properties, commandsHolder),
            new UntrackCommand(properties, new DefaultBotService())
        );
        Mockito.when(commandsHolder.getCommandHandlers()).thenReturn(commands);

        chainCommandsHandler = new ChainCommandsHandler(commandsHolder);
        chainCommandsHandler.setUpChain();
    }
}
