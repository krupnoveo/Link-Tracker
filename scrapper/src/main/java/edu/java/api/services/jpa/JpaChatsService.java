package edu.java.api.services.jpa;

import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatDoesNotExistException;
import edu.java.api.services.ChatsService;
import edu.java.domain.repository.jpa.JpaChatsRepository;
import edu.java.domain.repository.jpa.JpaLinksRepository;
import edu.java.domain.repository.jpa.entities.ChatEntity;
import edu.java.domain.repository.jpa.entities.LinkEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaChatsService implements ChatsService {
    private final JpaChatsRepository chatsRepository;
    private final JpaLinksRepository linksRepository;

    @Override
    @Transactional
    public void registerChat(long chatId) {
        if (chatsRepository.existsById(chatId)) {
            throw new ChatAlreadyRegisteredException(chatId);
        }
        chatsRepository.save(new ChatEntity(chatId));
    }

    @Override
    @Transactional
    public void deleteChat(long chatId) {
        ChatEntity chat = chatsRepository.findById(chatId).orElseThrow(() -> new ChatDoesNotExistException(chatId));
        for (LinkEntity link : chat.getLinks()) {
            chat.deleteLink(link);
            if (link.getChats().isEmpty()) {
                linksRepository.delete(link);
            }
        }
        chatsRepository.delete(chat);
    }
}
