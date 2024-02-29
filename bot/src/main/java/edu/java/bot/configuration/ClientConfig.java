package edu.java.bot.configuration;

import edu.java.bot.api.httpClient.ScrapperClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Bean
    public ScrapperClient scrapperClient() {
        return new ScrapperClient();
    }
}
