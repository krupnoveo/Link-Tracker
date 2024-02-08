package edu.java.bot.updatesListener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.ChainCommandsHandler;
import edu.java.bot.printerToChat.ChatResponser;
import edu.java.bot.printerToChat.DefaultChatResponser;

public class ChatUpdatesListener {
    private final TelegramBot bot;
    private final ChatResponser responser;
    private final ChainCommandsHandler chainCommandsHandler;

    public ChatUpdatesListener(TelegramBot bot) {
        this.bot = bot;
        this.responser = new DefaultChatResponser(bot);
        this.chainCommandsHandler = new ChainCommandsHandler();
    }

    public void setUpdatesListener() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                SendMessage responseMessage = chainCommandsHandler.handleCommand(update.message());
                responser.sendMessage(responseMessage);
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
