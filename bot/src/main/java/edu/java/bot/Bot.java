package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bot {
    @Autowired
    public void create(ApplicationConfig applicationConfig) {
        TelegramBot bot = new TelegramBot(applicationConfig.telegramToken());
        bot.setUpdatesListener(updates -> {
            updates.forEach(System.out::println);
            updates.forEach(update -> {
                if (update.message().text().equals("/start")) {
                    bot.execute(
                        new SendMessage(update.message().chat().id(),
                            "привет, " + update.message().chat().firstName()));
                } else {
                    bot.execute(new SendMessage(update.message().from().id(), "неизвестная команда"));
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
