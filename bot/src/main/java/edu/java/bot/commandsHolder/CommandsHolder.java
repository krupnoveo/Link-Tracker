package edu.java.bot.commandsHolder;

import edu.java.bot.commands.CommandHandler;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Getter
@Service
@ComponentScan(basePackages = "edu.java.bot.commands")
public class CommandsHolder {
    private final List<CommandHandler> commandHandlers;
    private final Map<String, String> commandsNameAndDescriptions;

    @Autowired
    public CommandsHolder(List<CommandHandler> commandHandlers, Properties properties) {
        this.commandHandlers = commandHandlers;
        this.commandsNameAndDescriptions = new LinkedHashMap<>();
        for (CommandHandler commandHandler : commandHandlers) {
            String commandName = commandHandler.commandName();
            if (!commandName.equals(properties.getProperty("command.start.name"))) {
                String commandDescription = commandHandler.commandDescription();
                commandsNameAndDescriptions.put(commandName, commandDescription);
            }
        }
    }
}
