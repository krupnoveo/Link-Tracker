package edu.java.bot.updatesListener;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.CommandHandler;
import edu.java.bot.commandsHolder.CommandsHolder;
import edu.java.bot.printerToChat.ChatResponser;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageUpdatesListener implements UpdatesListener {
    private final ChatResponser responser;
    private final CommandsHolder commandsHolder;
    private final String delimiter;
    private final String commandUnknown;
    private Counter messagesCounter;

    @Autowired
    public MessageUpdatesListener(
        ChatResponser responser,
        CommandsHolder commandsHolder,
        Properties properties,
        MeterRegistry registry
    ) {
        this.responser = responser;
        this.commandsHolder = commandsHolder;
        this.delimiter = properties.getProperty("command.untrack.callBackDelimiter");
        this.commandUnknown = properties.getProperty("command.unknown");
        this.messagesCounter = Counter
            .builder("handled_user_messages")
            .description("Count of handled user messages")
            .register(registry);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            SendMessage message = handleUpdate(update);
            if (message != null) {
                messagesCounter.increment();
                responser.sendMessage(message);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private SendMessage handleUpdate(Update update) {
        String command = getCommandFromUpdate(update);
        CommandHandler handler = commandsHolder.getCommandHandlers().get(command);
        SendMessage message = null;
        if (handler == null) {
            if (update.message() != null) {
                 message = new SendMessage(
                        update.message().chat().id(),
                        commandUnknown);
            } else if (update.callbackQuery() != null) {
                message = new SendMessage(
                        update.callbackQuery().from().id(),
                        commandUnknown);
            } else {
                message = new SendMessage(
                    update.editedMessage().chat().id(),
                    commandUnknown
                );
            }
            return message;
        }
        if (handler.isSupportsUpdate(update)) {
            message = handler.handleCommand(update);
        }

        return message;
    }

    private String getCommandFromUpdate(Update update) {
        if (update.message() != null && update.message().text() != null) {
            return update.message().text().trim().replaceAll("\\s+", " ").split(" ")[0];
        }
        if (update.callbackQuery() != null && update.callbackQuery().data() != null) {
            return update.callbackQuery().data().split(delimiter)[0];
        }
        return "";
    }
}
