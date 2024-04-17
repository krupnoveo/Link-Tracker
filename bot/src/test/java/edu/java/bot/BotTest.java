package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import edu.java.bot.api.httpClient.ScrapperClient;
import edu.java.bot.commands.CommandHandler;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.commandsHolder.CommandsHolder;
import edu.java.bot.clientService.BotService;
import edu.java.bot.clientService.DefaultBotService;
import edu.java.bot.updatesListener.MessageUpdatesListener;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@ExtendWith(MockitoExtension.class)
public class BotTest {
    @SneakyThrows
    @Test
    @DisplayName("Тест Bot.startUpBot()")
    public void startUpBot_shouldWorkCorrectly() {
        CommandsHolder commandsHolder = Mockito.mock(CommandsHolder.class);
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        ScrapperClient client = Mockito.mock(ScrapperClient.class);
        UpdatesListener listener = Mockito.mock(MessageUpdatesListener.class);
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        BotService botService = new DefaultBotService(client);
        Map<String, CommandHandler> commands = Map.of(
            properties.getProperty("command.start.name"), new StartCommand(properties, botService),
            properties.getProperty("command.help.name"), new HelpCommand(properties, commandsHolder),
            properties.getProperty("command.track.name"),new TrackCommand(properties, botService),
            properties.getProperty("command.untrack.name"),new UntrackCommand(properties, botService),
            properties.getProperty("command.list.name"),new ListCommand(properties, botService)
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

        Mockito.when(commandsHolder.getCommandsNameAndDescriptions()).thenReturn(commandsDescription);

        Bot bot = new Bot(commandsHolder, telegramBot, listener);
        bot.startUpBot();

        Mockito.verify(telegramBot).setUpdatesListener(Mockito.any(MessageUpdatesListener.class));
    }
}
