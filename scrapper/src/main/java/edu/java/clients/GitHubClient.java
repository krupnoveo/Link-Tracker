package edu.java.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.configuration.ApplicationConfig;
import edu.java.models.LinkData;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient extends BaseClient {

    private final static String HOST = "github.com";
    private final static Pattern URL_PATTERN = Pattern.compile("https://" + HOST + "/[\\w-]+/[\\w-]+");


    public GitHubClient(String baseUrl, ApplicationConfig config) {
        super(
            WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + config.gitHubToken());
                })
                .build()
        );
    }

    public GitHubClient(String url) {
        super(url);
    }


    @Override
    public LinkData checkURL(URL url) {
        GitHubResponse response = webClient
            .get()
            .uri("/repos" + url.getPath())
            .retrieve()
            .bodyToMono(GitHubResponse.class)
            .onErrorComplete()
            .block();
        if (response == null || response.lastUpdated() == null) {
            return new LinkData(null, null);
        }
        return new LinkData(url, response.lastUpdated());
    }

    @Override
    public boolean isUrlSupported(URL url) {
        Matcher matcher = URL_PATTERN.matcher(url.toString());
        return matcher.matches();
    }

    private record GitHubResponse(
        @JsonProperty("updated_at") OffsetDateTime lastUpdated
    ) {}
}
