package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.Link;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.service.BotService;
import java.util.Properties;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class UntrackCommand extends CommandHandler {

    private final BotService botService;
    private static final String DELIMITER = ":";

    @Autowired
    public UntrackCommand(Properties properties, BotService botService) {
        super(properties);
        this.botService = botService;
    }

    @Override
    public SendMessage handleCommand(Update update) {
        String[] text = update.message().text().split(" ");
        String command = text[0];
        Long id = update.message().chat().id();
        if (command.equals(commandName())) {
            SendMessage sendMessage;
            if (update.callbackQuery() == null) {
                sendMessage = handleMessage(id);
            } else {
                sendMessage = handleCallback(id, update);
            }
            return sendMessage;
        }
//        if (command.equals(commandName())) {
//            SendMessage sendMessage;
//            if (text.length > 1) {
//                String url = text[1];
//                RemoveLinkFromDatabaseResponse response = botService.removeLinkFromDatabase(UUID.randomUUID(), id);
//                if (response.status()) {
//                    sendMessage = new SendMessage(
//                        id,
//                        properties.getProperty("command.untrack.removeURL.success")
//                            .formatted(url)
//                    );
//                } else {
//                    sendMessage = new SendMessage(
//                        id,
//                        properties.getProperty("command.untrack.removeURL.fail")
//                            .formatted(response.responseFromDatabase())
//                    );
//                }
//            } else {
//                sendMessage = new SendMessage(
//                    id,
//                    properties.getProperty("command.untrack.missingURL")
//                );
//            }
//            return sendMessage;
//        }
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(update);
    }

    private SendMessage handleMessage(Long id) {
        ListLinksResponse response = botService.listLinksFromDatabase(id);
        if (response.links().isEmpty()) {
            return new SendMessage(
                id,
                properties.getProperty("command.untrack.empty")
            );
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : response.links()) {
            keyboardMarkup.addRow(
                new InlineKeyboardButton(link.url())
                    .callbackData(
                    commandName() + DELIMITER + link.uuid()
                )
            );
        }
        return new SendMessage(
            id,
            properties.getProperty("command.untrack.chooseLinkToRemove")
        ).parseMode(ParseMode.Markdown).replyMarkup(keyboardMarkup);
    }

    private SendMessage handleCallback(Long id, Update update) {
        String[] data = update.callbackQuery().data().split(DELIMITER);
        String uuid = data[1];

        RemoveLinkFromDatabaseResponse response = botService.removeLinkFromDatabase(UUID.fromString(uuid), id);
        if (response.status()) {
            return new SendMessage(
                id,
                properties.getProperty("command.untrack.removeURL.success")
            );
        } else {
            return new SendMessage(
                id,
                properties.getProperty("command.untrack.removeURL.fail").formatted(response.responseFromDatabase())
            );
        }
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
