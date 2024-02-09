package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListCommand extends CommandHandler {

    @Autowired
    public ListCommand(Properties properties) {
        super(properties);
    }

    @Override
    public SendMessage handleCommand(Message message) {
        String text = message.text();
        String command = text.split(" ")[0];
        Long id = message.chat().id();
        if (command.equals(commandName())) {
            return new SendMessage(
                id,
                "Список отслеживаемых адресов пуст. Можете добавить новый с помощью команды /track <адрес>");
        }
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(message);
    }

    @Override
    public String commandName() {
        return properties.getProperty("command.list.name");
    }

    @Override
    public String commandDescription() {
        return properties.getProperty("command.list.description");
    }
}
