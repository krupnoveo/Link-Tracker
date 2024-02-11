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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        SendMessage sendMessage;
        if (update.callbackQuery() == null) {
            sendMessage = handleMessage(update);
        } else {
            sendMessage = handleCallback(update);
        }
        return sendMessage;
    }

    private SendMessage handleMessage(Update update) {
        String[] text = update.message().text().split(" ");
        String command = text[0];
        Long id = update.message().chat().id();
        if (command.equals(commandName())) {
            ListLinksResponse response = botService.listLinksFromDatabase(id);
            if (response.links().isEmpty()) {
                return new SendMessage(
                    id,
                    properties.getProperty("command.untrack.empty")
                );
            }
            InlineKeyboardMarkup keyboardMarkup = makeKeyboard(response);
            return new SendMessage(
                id,
                properties.getProperty("command.untrack.chooseLinkToRemove")
            ).parseMode(ParseMode.Markdown).replyMarkup(keyboardMarkup);
        }
        return sendToTheNextHandler(id, update);
    }

    private SendMessage handleCallback(Update update) {
        Long id = update.callbackQuery().from().id();
        String[] data = update.callbackQuery().data().split(DELIMITER);
        String command = data[0];
        if (command.equals(commandName())) {
            String uuid = data[1];
            RemoveLinkFromDatabaseResponse response = botService.removeLinkFromDatabase(UUID.fromString(uuid), id);
            if (response.status()) {
                return new SendMessage(
                    id,
                    properties.getProperty("command.untrack.removeURL.success").formatted(response.link().url())
                );
            } else {
                return new SendMessage(
                    id,
                    properties.getProperty("command.untrack.removeURL.fail").formatted(response.responseFromDatabase())
                );
            }
        }
        return sendToTheNextHandler(id, update);
    }

    private InlineKeyboardMarkup makeKeyboard(ListLinksResponse response) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : response.links()) {
            keyboardMarkup.addRow(
                new InlineKeyboardButton(link.url())
                    .callbackData(
                        commandName() + DELIMITER + link.uuid()
                    )
            );
        }
        return keyboardMarkup;
    }

    private SendMessage sendToTheNextHandler(Long id, Update update) {
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(update);
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
