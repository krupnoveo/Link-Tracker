package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;

public class UntrackCommand extends CommandHandler {
    public UntrackCommand(Properties properties) {
        super(properties);
    }

    @Override
    public SendMessage handleCommand(Message message) {
        String[] text = message.text().split(" ");
        String command = text[0];
        Long id = message.chat().id();
        if (command.equals("/untrack")) {
            String url = text[1];
            return new SendMessage(
                id,
                "Адрес " + url + " успешно удалён из отслеживания.");
        }
        if (nextHandler == null) {
            return new SendMessage(id, UNKNOWN_COMMAND_REPLY);
        }
        return nextHandler.handleCommand(message);
    }
}
