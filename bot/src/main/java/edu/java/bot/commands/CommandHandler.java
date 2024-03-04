package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;

public abstract class CommandHandler {
    protected final Properties properties;
    protected CommandHandler nextHandler = null;

    public CommandHandler(Properties properties) {
        this.properties = properties;
    }

    public void setNextHandler(CommandHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract SendMessage handleCommand(Update update);

    public abstract String commandName();

    public abstract String commandDescription();
}
