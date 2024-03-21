package edu.java.configuration;

import edu.java.api.services.ChatsService;
import edu.java.api.services.LinksService;
import edu.java.api.services.jpa.JpaChatsService;
import edu.java.api.services.jpa.JpaLinksService;
import edu.java.clients.holder.ClientsHolder;
import edu.java.domain.repository.jpa.JpaChatsRepository;
import edu.java.domain.repository.jpa.JpaLinksRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public LinksService linksService(
        JpaLinksRepository linksRepository,
        JpaChatsRepository chatsRepository,
        ClientsHolder clientsHolder
    ) {
        return new JpaLinksService(linksRepository, chatsRepository, clientsHolder);
    }

    @Bean
    public ChatsService chatsService(
        JpaChatsRepository chatsRepository,
        JpaLinksRepository linksRepository
    ) {
        return new JpaChatsService(chatsRepository, linksRepository);
    }
}
