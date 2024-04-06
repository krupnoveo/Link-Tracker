package edu.java.bot.api.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.request.LinkUpdate;
import edu.java.bot.printerToChat.ChatResponser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class LinkUpdatesService {
    private final ChatResponser chatResponser;

    public void notifyUsers(List<LinkUpdate> linkUpdates) {
        log.info("Notifying users...");
        for (LinkUpdate linkUpdate : linkUpdates) {
            for (long chatId : linkUpdate.chatIds()) {
                SendMessage message = new SendMessage(
                    chatId,
                    linkUpdate.description().formatted(linkUpdate.url())
                );
                chatResponser.sendMessage(message);
            }
        }
        throw new RuntimeException();
    }
}
