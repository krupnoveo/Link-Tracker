package edu.java;

import edu.java.api.controllers.rateLimit.RateLimiterAspect;
import edu.java.configuration.ApplicationConfig;
import edu.java.configuration.RetryConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationConfig.class, RetryConfiguration.class, RateLimiterAspect.class})
public class ScrapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }
}
