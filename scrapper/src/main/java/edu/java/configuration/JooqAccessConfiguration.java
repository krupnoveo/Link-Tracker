package edu.java.configuration;

import edu.java.api.services.ChatsService;
import edu.java.api.services.LinksService;
import edu.java.api.services.jooq.JooqChatsService;
import edu.java.api.services.jooq.JooqLinksService;
import edu.java.clients.holder.ClientsHolder;
import edu.java.domain.ChatsRepository;
import edu.java.domain.ChatsToLinksRepository;
import edu.java.domain.LinksRepository;
import edu.java.domain.repository.jooq.JooqChatsRepository;
import edu.java.domain.repository.jooq.JooqChatsToLinksRepository;
import edu.java.domain.repository.jooq.JooqLinksRepository;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    @Bean
    public LinksService linksService(
        LinksRepository linksRepository,
        ChatsToLinksRepository chatsToLinksRepository,
        ClientsHolder clientsHolder
    ) {
        return new JooqLinksService(linksRepository, chatsToLinksRepository, clientsHolder);
    }

    @Bean
    public ChatsService chatsService(
        ChatsRepository chatsRepository,
        LinksRepository linksRepository,
        ChatsToLinksRepository chatsToLinksRepository
    ) {
        return new JooqChatsService(chatsRepository, linksRepository, chatsToLinksRepository);
    }

    @Bean
    public ChatsRepository chatsRepository(DSLContext context) {
        return new JooqChatsRepository(context);
    }

    @Bean
    public LinksRepository linksRepository(DSLContext context) {
        return new JooqLinksRepository(context);
    }

    @Bean
    public ChatsToLinksRepository chatsToLinksRepository(DSLContext context) {
        return new JooqChatsToLinksRepository(context);
    }
}
