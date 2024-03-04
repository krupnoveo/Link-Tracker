package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.service.BotService;
import java.util.Arrays;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand extends CommandHandler {

    private final BotService botService;

    @Autowired
    public TrackCommand(Properties properties, BotService botService) {
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
        String[] text = update.message().text().replaceAll("\\s+", " ").split(" ");
        String command = text[0];
        Long id = update.message().chat().id();
        if (command.equals(commandName())) {
            SendMessage sendMessage;
            if (text.length > 1) {
                String url = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                AddLinkToDatabaseResponse response = botService.addLinkToDatabase(url, id);
                if (response.status()) {
                    sendMessage = new SendMessage(
                        id,
                        properties.getProperty("command.track.addURL.success")
                            .formatted(url)
                    );
                } else {
                    sendMessage = new SendMessage(
                        id,
                        properties.getProperty("command.track.addURL.fail")
                            .formatted(response.responseFromDatabase())
                    );
                }
            } else {
                sendMessage = new SendMessage(
                    id,
                    properties.getProperty("command.track.missingURL")
                );
            }
            return sendMessage;
        }
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(update);
    }

    @Override
    public String commandName() {
        return properties.getProperty("command.track.name");
    }

    @Override
    public String commandDescription() {
        return properties.getProperty("command.track.description");
    }
}
