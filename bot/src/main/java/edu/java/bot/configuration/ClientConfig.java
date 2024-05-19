package edu.java.bot.configuration;

import edu.java.bot.api.httpClient.ScrapperClient;
import edu.java.bot.configuration.RetryConfiguration.Client;
import edu.java.bot.util.RetryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Bean
    public ScrapperClient scrapperClient(RetryConfiguration retryConfiguration) {
        return new ScrapperClient(RetryFactory.createRule(retryConfiguration.clientConfigs(), Client.SCRAPPER));
    }
}
