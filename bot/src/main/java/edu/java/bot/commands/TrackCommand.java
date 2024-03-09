package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.clientService.BotService;
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
        String[] text = update.message().text().trim().replaceAll("\\s+", " ").split(" ");
        long chatId = update.message().chat().id();
        if (text.length > 2) {
            return new SendMessage(
                chatId,
                properties.getProperty("command.track.severalURLs")
            );
        }
        if (text.length == 2) {
            GenericResponse<AddLinkToDatabaseResponse> response = botService.addLinkToDatabase(text[1], chatId);
            if (response.errorResponse() == null) {
                return new SendMessage(
                    chatId,
                    properties.getProperty("command.track.addURL.success")
                        .formatted(response.response().url())
                );
            }
            return new SendMessage(
                chatId,
                properties.getProperty("command.track.addURL.fail")
                    .formatted(response.errorResponse().description().toLowerCase())
            );
        }
        return new SendMessage(
            chatId,
            properties.getProperty("command.track.missingURL")
        );
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
