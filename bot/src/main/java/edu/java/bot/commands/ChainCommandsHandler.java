package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;
import lombok.SneakyThrows;

public class ChainCommandsHandler {
    private final CommandHandler startHandler;
    private final CommandHandler helpHandler;
    private final CommandHandler trackHandler;
    private final CommandHandler untrackHandler;
    private final CommandHandler listHandler;

    @SneakyThrows
    public ChainCommandsHandler() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        startHandler = new StartCommand(properties);
        helpHandler = new HelpCommand(properties);
        trackHandler = new TrackCommand(properties);
        untrackHandler = new UntrackCommand(properties);
        listHandler = new ListCommand(properties);
    }

    public SendMessage handleCommand(Message message) {
        startHandler
            .setNextHandler(helpHandler)
            .setNextHandler(trackHandler)
            .setNextHandler(untrackHandler)
            .setNextHandler(listHandler);
        return startHandler.handleCommand(message);
    }
}
