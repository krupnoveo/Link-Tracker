package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartCommand extends CommandHandler {

    @Autowired
    public StartCommand(Properties properties) {
        super(properties);
    }

    @Override
    public SendMessage handleCommand(Message message) {
        String[] text = message.text().split(" ");
        String command = text[0];
        Long id = message.chat().id();
        if (command.equals(commandName())) {
            return new SendMessage(
                id,
                properties.getProperty("command.start.hello")
            ).parseMode(ParseMode.Markdown);
        }
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(message);
    }

    @Override
    public String commandName() {
        return properties.getProperty("command.start.name");
    }

    @Override
    public String commandDescription() {
        return properties.getProperty("command.start.description");
    }
}
