package edu.java.configuration;

import edu.java.api.httpClient.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.configuration.RetryConfiguration.Client;
import edu.java.util.RetryFactory;
import java.util.Map;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
@Configuration
public class ClientConfiguration {

    @Bean
    public GitHubClient gitHubClient(
        @Value("${clients.github.base-url}") String baseUrl,
        @Value("${clients.github.token}") String gitHubToken,
        Properties properties,
        RetryConfiguration retryConfiguration
    ) {
        WebClient client = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .defaultHeaders(httpHeaders -> {
                httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + gitHubToken);
            })
            .build();
        Map response = client
            .get()
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        if (response != null) {
            String message = (String) response.get("message");
            if (message != null && message.equals("Bad credentials")) {
                log.info(
                    "Invalid GitHub token. Creating GitHubClient object without authentication token. Rate limit is 60"
                );
                return new GitHubClient(
                    baseUrl, properties, RetryFactory.createRule(retryConfiguration.clientConfigs(), Client.GITHUB)
                );
            }
        }
        log.info("Valid GitHub token. Creating GitHubClient object with authentication token. Rate limit is 5000");
        return new GitHubClient(
            baseUrl, gitHubToken, properties, RetryFactory.createRule(retryConfiguration.clientConfigs(), Client.GITHUB)
        );
    }

    @Bean
    public StackOverflowClient stackOverflowClient(
        @Value("${clients.stackoverflow.base-url}") String baseUrl,
        @Value("${clients.stackoverflow.key}") String key,
        @Value("${clients.stackoverflow.token}") String token,
        Properties properties,
        RetryConfiguration retryConfiguration
    ) {
        WebClient client = WebClient.builder()
            .baseUrl(baseUrl + "/questions?site=stackoverflow&access_token=" + token + "&key=" + key)
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .build();
        Map response = client
            .get()
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        if (response != null) {
            Integer errorId = (Integer) response.get("error_id");
            if (errorId != null
                && (errorId == HttpStatus.BAD_REQUEST.value() || errorId == HttpStatus.FORBIDDEN.value())) {
                log.info("Invalid StackOverflow key or/and token. Initializing without key and token. "
                     + "Rate limit is 300 per day");
                return new StackOverflowClient(
                    baseUrl,
                    properties,
                    RetryFactory.createRule(retryConfiguration.clientConfigs(), Client.STACKOVERFLOW)
                );
            }
        }
        log.info("Valid StackOverflow key and token. Initializing with key and token. Rate limit is 10000 per day");
        return new StackOverflowClient(
            baseUrl,
            properties,
            key,
            token,
            RetryFactory.createRule(retryConfiguration.clientConfigs(), Client.STACKOVERFLOW)
        );
    }

    @Bean
    public BotClient botClient(RetryConfiguration retryConfiguration) {
        return new BotClient(RetryFactory.createRule(retryConfiguration.clientConfigs(), Client.BOT));
    }
}
