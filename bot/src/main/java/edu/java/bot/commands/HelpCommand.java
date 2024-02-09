package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand extends CommandHandler {

    @Autowired
    public HelpCommand(Properties properties) {
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
                """
                <b>Список команд:</b>
                /track <адрес> - добавление адреса для отслеживания
                /untrack <адрес> - удаление адреса из отслеживания
                /list - вывод списка всех отслеживаемых адресов""");
        }
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(message);
    }

    @Override
    public String commandName() {
        return properties.getProperty("command.help.name");
    }

    @Override
    public String commandDescription() {
        return properties.getProperty("command.help.description");
    }
}
