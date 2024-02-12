package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.commands.CommandHandler;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.commandsHolder.CommandsHolder;
import edu.java.bot.service.BotService;
import edu.java.bot.service.DefaultBotService;
import edu.java.bot.updatesListener.MessageUpdatesListener;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@ExtendWith(MockitoExtension.class)
public class BotTest {
    @SneakyThrows
    @Test
    public void startUpBot_shouldWorkCorrectly() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        BotService botService = new DefaultBotService();
        CommandsHolder commandsHolder = Mockito.mock(CommandsHolder.class);
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        List<CommandHandler> commands = List.of(
            new StartCommand(properties, botService),
            new HelpCommand(properties, commandsHolder),
            new TrackCommand(properties, botService),
            new UntrackCommand(properties, botService),
            new UntrackCommand(properties, botService)
        );
        Map<String, String> commandsDescription = new LinkedHashMap<>();
        commandsDescription.put(
            properties.getProperty("command.help.name"), properties.getProperty("command.help.description")
        );
        commandsDescription.put(
            properties.getProperty("command.track.name"), properties.getProperty("command.track.description")
        );
        commandsDescription.put(
            properties.getProperty("command.untrack.name"), properties.getProperty("command.untrack.description")
        );
        commandsDescription.put(
            properties.getProperty("command.list.name"), properties.getProperty("command.list.description")
        );
        Mockito.when(commandsHolder.getCommandHandlers()).thenReturn(commands);
        Mockito.when(commandsHolder.getCommandsNameAndDescriptions()).thenReturn(commandsDescription);

        Bot bot = new Bot(commandsHolder, telegramBot);
        bot.startUpBot();

        Mockito.verify(telegramBot).setUpdatesListener(Mockito.any(MessageUpdatesListener.class));
    }
}
