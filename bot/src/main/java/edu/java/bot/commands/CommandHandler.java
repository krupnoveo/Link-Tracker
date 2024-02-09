package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;

public abstract class CommandHandler {
    protected final Properties properties;
    protected CommandHandler nextHandler = null;

    public CommandHandler(Properties properties) {
        this.properties = properties;
    }

    public CommandHandler setNextHandler(CommandHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public abstract SendMessage handleCommand(Message message);

    public abstract String commandName();

    public abstract String commandDescription();
}
