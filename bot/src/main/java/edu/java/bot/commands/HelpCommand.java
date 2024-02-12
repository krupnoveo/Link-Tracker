package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commandsHolder.CommandsHolder;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand extends CommandHandler {
    private final CommandsHolder commandsHolder;

    @Autowired
    public HelpCommand(Properties properties, @Lazy CommandsHolder commandsHolder) {
        super(properties);
        this.commandsHolder = commandsHolder;
    }

    @Override
    public SendMessage handleCommand(Update update) {
        if (update.message() != null) {
            return processCommand(update);
        }
        return nextHandler.handleCommand(update);
    }

    private SendMessage processCommand(Update update) {
        String text = update.message().text();
        String command = text.split(" ")[0];
        Long id = update.message().chat().id();
        if (command.equals(commandName())) {
            Map<String, String> commandsNameAndDescription = commandsHolder.getCommandsNameAndDescriptions();
            StringBuilder response = new StringBuilder(properties.getProperty("command.help.listCommands"));
            for (Map.Entry<String, String> entry : commandsNameAndDescription.entrySet()) {
                response
                    .append("\n")
                    .append(entry.getKey())
                    .append(" - ")
                    .append(entry.getValue().toLowerCase());
            }
            return new SendMessage(
                id,
                response.toString()
            ).parseMode(ParseMode.Markdown);
        }
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(update);
    }

    @Override
    public String commandName() {
        return properties.getProperty("command.help.name");
    }

    @Override
    public String commandDescription() {
        return properties.getProperty("command.help.description");
    }
}
