package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.Link;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.clientService.BotService;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends CommandHandler {

    private final BotService botService;
    private final String delimiter;

    @Autowired
    public UntrackCommand(Properties properties, BotService botService) {
        super(properties);
        this.delimiter = properties.getProperty("command.untrack.callBackDelimiter");
        this.botService = botService;
    }

    @Override
    public boolean isSupportsUpdate(Update update) {
        return
            (super.isSupportsUpdate(update))
                || (update.callbackQuery() != null && update.callbackQuery().data() != null);
    }

    @Override
    public SendMessage handleCommand(Update update) {
        if (update.callbackQuery() == null) {
            return handleMessage(update);
        }
        return handleCallback(update);
    }

    private SendMessage handleMessage(Update update) {
        long chatId = update.message().chat().id();
        GenericResponse<ListLinksResponse> response = botService.listLinksFromDatabase(chatId);
        if (response.errorResponse() == null) {
            if (response.response().links().isEmpty()) {
                return new SendMessage(
                    chatId,
                    properties.getProperty("command.untrack.empty")
                );
            }
            InlineKeyboardMarkup keyboardMarkup = makeKeyboard(response.response());
            return new SendMessage(
                chatId,
                properties.getProperty("command.untrack.chooseLinkToRemove")
            ).parseMode(ParseMode.Markdown).replyMarkup(keyboardMarkup);
        }
        return new SendMessage(
            chatId,
            properties.getProperty("command.untrack.handleCommand.error")
                .formatted(response.errorResponse().description().toLowerCase())
        );
    }

    private SendMessage handleCallback(Update update) {
        long chatId = update.callbackQuery().from().id();
        String urlId = update.callbackQuery().data().split(delimiter)[1];
        GenericResponse<RemoveLinkFromDatabaseResponse> response = botService.removeLinkFromDatabase(
            Long.parseLong(urlId), chatId
        );
        if (response.errorResponse() == null) {
            return new SendMessage(
                chatId,
                properties.getProperty("command.untrack.removeURL.success")
                    .formatted(response.response().url())
            );
        }
        return new SendMessage(
            chatId,
            properties.getProperty("command.untrack.removeURL.fail")
                .formatted(response.errorResponse().description().toLowerCase())
        );
    }


    private InlineKeyboardMarkup makeKeyboard(ListLinksResponse response) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : response.links()) {
            keyboardMarkup.addRow(
                new InlineKeyboardButton(link.url().toString())
                    .callbackData(
                        commandName() + delimiter + link.id()
                    )
            );
        }
        return keyboardMarkup;
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
