package edu.java.configuration;

import edu.java.api.httpClient.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
@Configuration
public class ClientConfiguration {

    @Bean
    public GitHubClient gitHubClient(@Value("${clients.github.base-url}") String baseUrl, ApplicationConfig config) {
        WebClient client = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .defaultHeaders(httpHeaders -> {
                httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + config.gitHubToken());
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
                return new GitHubClient(baseUrl);
            }
        }
        log.info("Valid GitHub token. Creating GitHubClient object with authentication token. Rate limit is 5000");
        return new GitHubClient(baseUrl, config);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(@Value("${clients.stackoverflow.base-url}") String baseUrl) {
        return new StackOverflowClient(baseUrl);
    }

    @Bean
    public BotClient botClient() {
        return new BotClient();
    }
}
