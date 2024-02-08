package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;

public class StartCommand extends CommandHandler {

    public StartCommand(Properties properties) {
        super(properties);
    }

    @Override
    public SendMessage handleCommand(Message message) {
        String text = message.text();
        String command = text.split(" ")[0];
        Long id = message.chat().id();
        if (command.equals("/start")) {
            return new SendMessage(
                id,
                properties.getProperty("start")).parseMode(
                ParseMode.Markdown);
        }
        if (nextHandler == null) {
            return new SendMessage(id, UNKNOWN_COMMAND_REPLY);
        }
        return nextHandler.handleCommand(message);
    }
}
