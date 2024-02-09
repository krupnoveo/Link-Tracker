package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends CommandHandler {

    @Autowired
    public UntrackCommand(Properties properties) {
        super(properties);
    }

    @Override
    public SendMessage handleCommand(Message message) {
        String[] text = message.text().split(" ");
        String command = text[0];
        Long id = message.chat().id();
        if (command.equals(commandName())) {
            if (text.length > 1) {
                String url = text[1];
                return new SendMessage(
                    id,
                    "Адрес " + url + " успешно удалён из отслеживания.");
            } else {
                return new SendMessage(
                    id,
                    "Необходимо указать адрес, который Вы хотите удалить. Повторите запрос."
                );
            }
        }
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(message);
    }

    @Override
    public String commandName() {
        return properties.getProperty("command.untrack.name");
    }

    @Override
    public String commandDescription() {
        return properties.getProperty("command.untrack.description");
    }
}
