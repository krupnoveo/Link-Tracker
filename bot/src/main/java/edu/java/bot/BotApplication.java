package edu.java.bot;

import edu.java.bot.api.controllers.rateLimit.RateLimiterAspect;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.configuration.RetryConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationConfig.class, RetryConfiguration.class, RateLimiterAspect.class})
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
