package edu.java.configuration;

import edu.java.api.services.ChatsService;
import edu.java.api.services.LinksService;
import edu.java.api.services.jdbc.JdbcChatsService;
import edu.java.api.services.jdbc.JdbcLinksService;
import edu.java.clients.holder.ClientsHolder;
import edu.java.domain.ChatsRepository;
import edu.java.domain.ChatsToLinksRepository;
import edu.java.domain.LinksRepository;
import edu.java.domain.repository.jdbc.JdbcChatsRepository;
import edu.java.domain.repository.jdbc.JdbcChatsToLinksRepository;
import edu.java.domain.repository.jdbc.JdbcLinksRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public LinksService linksService(
        LinksRepository linksRepository,
        ChatsToLinksRepository chatsToLinksRepository,
        ClientsHolder clientsHolder
    ) {
        return new JdbcLinksService(linksRepository, chatsToLinksRepository, clientsHolder);
    }

    @Bean
    public ChatsService chatsService(
        ChatsRepository chatsRepository,
        LinksRepository linksRepository,
        ChatsToLinksRepository chatsToLinksRepository
    ) {
        return new JdbcChatsService(chatsRepository, linksRepository, chatsToLinksRepository);
    }

    @Bean
    public ChatsRepository chatsRepository(JdbcClient client) {
        return new JdbcChatsRepository(client);
    }

    @Bean
    public LinksRepository linksRepository(JdbcClient client) {
        return new JdbcLinksRepository(client);
    }

    @Bean
    public ChatsToLinksRepository chatsToLinksRepository(JdbcClient client) {
        return new JdbcChatsToLinksRepository(client);
    }
}
