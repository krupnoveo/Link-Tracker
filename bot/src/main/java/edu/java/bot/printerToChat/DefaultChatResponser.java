package edu.java.bot.printerToChat;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class DefaultChatResponser implements ChatResponser {
    private final TelegramBot bot;

    public DefaultChatResponser(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public void sendMessage(SendMessage message) {
        bot.execute(message);
    }
}
