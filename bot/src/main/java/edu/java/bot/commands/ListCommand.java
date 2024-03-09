package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.GenericResponse;
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
        long chatId = update.message().chat().id();
        GenericResponse<ListLinksResponse> response = botService.listLinksFromDatabase(chatId);
        if (response.errorResponse() == null) {
            if (response.response().links().isEmpty()) {
                return new SendMessage(
                    chatId,
                    properties.getProperty("command.list.empty")
                );
            }
            InlineKeyboardMarkup keyboardMarkup = makeKeyboard(response.response());
            return new SendMessage(
                chatId,
                properties.getProperty("command.list.listLinks.success")
            ).replyMarkup(keyboardMarkup).parseMode(ParseMode.Markdown);
        }
        return new SendMessage(
            chatId,
            properties.getProperty("command.list.listLinks.fail")
                .formatted(response.errorResponse().description().toLowerCase())
        );
    }


    private InlineKeyboardMarkup makeKeyboard(ListLinksResponse response) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : response.links()) {
            keyboardMarkup.addRow(new InlineKeyboardButton(link.url().toString()).url(link.url().toString()));
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
