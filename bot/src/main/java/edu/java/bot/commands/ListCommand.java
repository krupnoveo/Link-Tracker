package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;

public class ListCommand extends CommandHandler {
    public ListCommand(Properties properties) {
        super(properties);
    }

    @Override
    public SendMessage handleCommand(Message message) {
        String text = message.text();
        String command = text.split(" ")[0];
        Long id = message.chat().id();
        if (command.equals("/list")) {
            return new SendMessage(
                id,
                "Список отслеживаемых адресов пуст. Можете добавить новый с помощью команды /track <адрес>");
        }
        if (nextHandler == null) {
            return new SendMessage(id, UNKNOWN_COMMAND_REPLY);
        }
        return nextHandler.handleCommand(message);
    }
}
