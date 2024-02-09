package edu.java.bot.commandsHolder;

import edu.java.bot.commands.CommandHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public CommandsHolder(List<CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
        this.commandsNameAndDescriptions = new HashMap<>();
        for (CommandHandler commandHandler : commandHandlers) {
            String commandName = commandHandler.commandName();
            String commandDescription = commandHandler.commandDescription();
            commandsNameAndDescriptions.put(commandName, commandDescription);
        }
    }
}
