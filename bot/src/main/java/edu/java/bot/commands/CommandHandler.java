package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;

public abstract class CommandHandler {
    protected final Properties properties;
    protected CommandHandler nextHandler = null;
    protected static final String UNKNOWN_COMMAND_REPLY = "Неизвестная команда. Повторите запрос.";

    public CommandHandler(Properties properties) {
        this.properties = properties;
    }

    public CommandHandler setNextHandler(CommandHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public abstract SendMessage handleCommand(Message message);
}
