package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;

public abstract class CommandHandler {
    protected final Properties properties;

    public CommandHandler(Properties properties) {
        this.properties = properties;
    }

    public boolean isSupportsUpdate(Update update) {
        return
            update.message() != null
                && update.message().text() != null;
    }

    public abstract SendMessage handleCommand(Update update);


    public abstract String commandName();

    public abstract String commandDescription();
}
