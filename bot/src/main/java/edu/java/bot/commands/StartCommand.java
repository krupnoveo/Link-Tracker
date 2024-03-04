package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.User;
import edu.java.bot.service.BotService;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartCommand extends CommandHandler {

    private final BotService botService;

    @Autowired
    public StartCommand(Properties properties, BotService botService) {
        super(properties);
        this.botService = botService;
    }

    @Override
    public SendMessage handleCommand(Update update) {
        if (update.message() != null) {
            return processCommand(update);
        }
        return nextHandler.handleCommand(update);
    }

    private SendMessage processCommand(Update update) {
        String[] text = update.message().text().split(" ");
        String command = text[0];
        Long id = update.message().chat().id();
        if (command.equals(commandName())) {
            String name = update.message().chat().firstName();
            if (botService.registerUser(new User(id, name))) {
                return new SendMessage(
                    id,
                    properties.getProperty("command.start.hello")
                ).parseMode(ParseMode.Markdown);
            } else {
                return new SendMessage(
                    id,
                    properties.getProperty("command.start.failedRegistration")
                );
            }
        }
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(update);
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
