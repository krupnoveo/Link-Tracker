package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.updatesListener.ChatUpdatesListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bot {
    @Autowired
    public void startBot(ApplicationConfig applicationConfig) {
        TelegramBot bot = new TelegramBot(applicationConfig.telegramToken());
        ChatUpdatesListener listener = new ChatUpdatesListener(bot);
        listener.setUpdatesListener();
    }
}
