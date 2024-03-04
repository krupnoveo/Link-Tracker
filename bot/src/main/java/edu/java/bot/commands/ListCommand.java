package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.Link;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.service.BotService;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListCommand extends CommandHandler {

    private final BotService botService;

    @Autowired
    public ListCommand(Properties properties, BotService botService) {
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
        String text = update.message().text();
        String command = text.split(" ")[0];
        Long id = update.message().chat().id();
        if (command.equals(commandName())) {
            ListLinksResponse response = botService.listLinksFromDatabase(id);
            if (response.links() == null || response.links().isEmpty()) {
                return new SendMessage(
                    id,
                    properties.getProperty("command.list.empty")
                );
            } else {
                InlineKeyboardMarkup keyboardMarkup = makeKeyboard(response);
                return new SendMessage(
                    id,
                    properties.getProperty("command.list.notEmpty")
                ).replyMarkup(keyboardMarkup).parseMode(ParseMode.Markdown);
            }
        }
        if (nextHandler == null) {
            return new SendMessage(id, properties.getProperty("command.unknown"));
        }
        return nextHandler.handleCommand(update);
    }

    private InlineKeyboardMarkup makeKeyboard(ListLinksResponse response) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : response.links()) {
            keyboardMarkup.addRow(new InlineKeyboardButton(link.url()).url(link.url()));
        }
        return keyboardMarkup;
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
