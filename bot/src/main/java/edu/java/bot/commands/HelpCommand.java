package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;

public class HelpCommand extends CommandHandler {

    public HelpCommand(Properties properties) {
        super(properties);
    }

    @Override
    public SendMessage handleCommand(Message message) {
        String text = message.text();
        String command = text.split(" ")[0];
        Long id = message.chat().id();
        if (command.equals("/help")) {
            return new SendMessage(
                id,
                """
                <b>Список команд:</b>
                /track <адрес> - добавление адреса для отслеживания
                /untrack <адрес> - удаление адреса из отслеживания
                /list - вывод списка всех отслеживаемых адресов""");
        }
        if (nextHandler == null) {
            return new SendMessage(id, UNKNOWN_COMMAND_REPLY);
        }
        return nextHandler.handleCommand(message);
    }
}
