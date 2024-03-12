package edu.java.api.services.jdbc;

import edu.java.api.services.ChatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class JdbcChatService implements ChatService {
    public void registerChat(long chatId) {
        log.info("Registering chat...");
    }

    public void deleteChat(long chatId) {
        log.info("Deleting chat...");
    }
}