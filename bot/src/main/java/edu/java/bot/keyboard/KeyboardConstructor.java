package edu.java.bot.keyboard;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import edu.java.bot.models.Link;
import java.util.List;

public final class KeyboardConstructor {
    private KeyboardConstructor() {}

    public static Keyboard makeUrlListKeyboard(List<Link> links) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        for (Link link : links) {
            keyboardMarkup.addRow(new InlineKeyboardButton(link.url()).url(link.url()));
        }
        return keyboardMarkup;
    }
}
