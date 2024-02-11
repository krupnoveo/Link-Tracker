package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commandsHolder.CommandsHolder;
import java.util.List;
import lombok.SneakyThrows;

public class ChainCommandsHandler {

    private final CommandsHolder commandsHolder;
    private final CommandHandler startHandler;

    @SneakyThrows
    public ChainCommandsHandler(CommandsHolder commandsHolder) {
        this.commandsHolder = commandsHolder;
        this.startHandler = commandsHolder.getCommandHandlers().get(0);
        setUpChain();
    }

    public void setUpChain() {
        List<CommandHandler> commandHandlerList = commandsHolder.getCommandHandlers();
        for (int i = 1; i < commandHandlerList.size(); i++) {
            commandHandlerList.get(i - 1)
                .setNextHandler(commandHandlerList.get(i));
        }
    }

    public SendMessage handleCommand(Update update) {
        return startHandler.handleCommand(update);
    }
}
