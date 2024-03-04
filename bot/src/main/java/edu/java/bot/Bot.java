package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commandsHolder.CommandsHolder;
import edu.java.bot.updatesListener.MessageUpdatesListener;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bot {
    private final CommandsHolder commandsHolder;
    private final TelegramBot bot;

    @Autowired
    public Bot(CommandsHolder commandsHolder, TelegramBot bot) {
        this.commandsHolder = commandsHolder;
        this.bot = bot;
    }

    @PostConstruct
    public void startUpBot() {
        setUpCommandsList();
        setUpUpdatesListener();
    }


    private void setUpCommandsList() {
        Map<String, String> commandsNameAndDescriptions = commandsHolder.getCommandsNameAndDescriptions();
        BotCommand[] menuCommands = new BotCommand[commandsNameAndDescriptions.size()];
        int index = 0;
        for (Map.Entry<String, String> entry : commandsNameAndDescriptions.entrySet()) {
            menuCommands[index] = new BotCommand(entry.getKey(), entry.getValue());
            index++;
        }
        SetMyCommands setMyCommands = new SetMyCommands(menuCommands);
        bot.execute(setMyCommands);
    }

    private void setUpUpdatesListener() {
        MessageUpdatesListener listener = new MessageUpdatesListener(bot, commandsHolder);
        bot.setUpdatesListener(listener);
    }
}
