package edu.java.bot.updatesListener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.ChainCommandsHandler;
import edu.java.bot.commandsHolder.CommandsHolder;
import edu.java.bot.printerToChat.ChatResponser;
import edu.java.bot.printerToChat.DefaultChatResponser;

public class MessageUpdatesListener {
    private final TelegramBot bot;
    private final ChatResponser responser;
    private final ChainCommandsHandler chainCommandsHandler;

    public MessageUpdatesListener(TelegramBot bot, CommandsHolder commandsHolder) {
        this.bot = bot;
        this.responser = new DefaultChatResponser(bot);
        this.chainCommandsHandler = new ChainCommandsHandler(commandsHolder);
    }

    public void setUpdatesListener() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                SendMessage responseMessage = chainCommandsHandler.handleCommand(update);
                responser.sendMessage(responseMessage);
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
