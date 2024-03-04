package edu.java.bot.commandsHolder;

import edu.java.bot.commands.CommandHandler;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.service.BotService;
import edu.java.bot.service.DefaultBotService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CommandsHolderTest {
    private CommandsHolder commandsHolder;
    @SneakyThrows
    @DisplayName("Тест CommandsHolder.getCommandHandlers() и .getCommandsNameAndDescriptions()")
    @Test
    public void getCommandHandlers_and_getCommandsNameAndDescriptions_shouldReturnCorrectAnswer() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        List<CommandHandler> commandHandlerList = getCommandHandlers(properties);
        commandsHolder = new CommandsHolder(commandHandlerList, properties);
        Map<String, String> commandsNameAndDescriptions = new LinkedHashMap<>();
        commandsNameAndDescriptions.put(
            properties.getProperty("command.list.name"), properties.getProperty("command.list.description")
        );
        commandsNameAndDescriptions.put(
            properties.getProperty("command.track.name"), properties.getProperty("command.track.description")
        );
        commandsNameAndDescriptions.put(
            properties.getProperty("command.untrack.name"), properties.getProperty("command.untrack.description")
        );
        commandsNameAndDescriptions.put(
            properties.getProperty("command.help.name"), properties.getProperty("command.help.description")
        );

        assertThat(commandsHolder.getCommandHandlers()).containsExactlyInAnyOrderElementsOf(commandHandlerList);
        assertThat(commandsHolder.getCommandsNameAndDescriptions()).containsExactlyInAnyOrderEntriesOf(commandsNameAndDescriptions);
    }

    private List<CommandHandler> getCommandHandlers(Properties properties) {
        BotService botService = new DefaultBotService();
        CommandHandler startCommand = new StartCommand(properties, botService);
        CommandHandler listCommand = new ListCommand(properties, botService);
        CommandHandler trackCommand = new TrackCommand(properties, botService);
        CommandHandler untrackCommand = new UntrackCommand(properties, botService);
        CommandHandler helpCommand = new HelpCommand(properties, commandsHolder);
        return List.of(
            startCommand,
            listCommand,
            trackCommand,
            untrackCommand,
            helpCommand
        );
    }
}
