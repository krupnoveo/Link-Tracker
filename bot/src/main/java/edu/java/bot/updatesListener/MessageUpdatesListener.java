package edu.java.bot.updatesListener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.ChainCommandsHandler;
import edu.java.bot.commandsHolder.CommandsHolder;
import edu.java.bot.printerToChat.ChatResponser;
import edu.java.bot.printerToChat.DefaultChatResponser;
import java.util.List;

public class MessageUpdatesListener implements UpdatesListener {
    private final TelegramBot bot;
    private final ChatResponser responser;
    private final ChainCommandsHandler chainCommandsHandler;

    public MessageUpdatesListener(TelegramBot bot, CommandsHolder commandsHolder) {
        this.bot = bot;
        this.responser = new DefaultChatResponser(bot);
        this.chainCommandsHandler = new ChainCommandsHandler(commandsHolder);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            SendMessage responseMessage = chainCommandsHandler.handleCommand(update);
            responser.sendMessage(responseMessage);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
