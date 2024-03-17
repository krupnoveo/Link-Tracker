package edu.java.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.models.LinkData;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient extends BaseClient {
    private String type;
    private String ref;
    private String refType;
    private String commitsCount;

    public GitHubClient(String baseUrl, String gitHubToken, Properties properties) {
        super(
            WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + gitHubToken);
                })
                .build(),
            properties
        );
        initializeFields();
    }

    public GitHubClient(String url, Properties properties) {
        super(url, properties);
        initializeFields();
    }

    private void initializeFields() {
        this.host = properties.getProperty("github.host");
        this.urlPattern = Pattern.compile("https://" + host + "/[\\w-]+/[\\w-]+");
        this.type = properties.getProperty("github.parameter.type.name");
        this.ref = properties.getProperty("github.parameter.ref.name");
        this.refType = properties.getProperty("github.parameter.refType.name");
        this.commitsCount = properties.getProperty("github.parameter.commitsCount.name");
    }


    @Override
    public List<LinkData> checkURL(URL url, OffsetDateTime lastUpdated) {
        GitHubEvent[] response = webClient
            .get()
            .uri("/repos" + url.getPath() + "/events?per_page=10")
            .retrieve()
            .bodyToMono(GitHubEvent[].class)
            .onErrorComplete()
            .block();
        if (response == null || response.length == 0 || response[0].getLastUpdated() == null) {
            return List.of(new LinkData(null, null, host, null));
        }
        List<LinkData> data = new ArrayList<>();
        if (lastUpdated == null) {
            return List.of(
                new LinkData(
                    url,
                    response[0].getLastUpdated(),
                    host,
                    Map.of(
                        type, response[0].getType() == null ? "" : response[0].getType(),
                        ref, response[0].getReference() == null ? "" : response[0].getReference(),
                        refType, response[0].getReferenceType() == null ? "" : response[0].getReferenceType(),
                        commitsCount, String.valueOf(
                            response[0].getCommitsCounter() == null ? "" : response[0].getCommitsCounter()
                        )
                    )
                )
            );
        }
        for (GitHubEvent event : response) {
            if (event.getLastUpdated().isAfter(lastUpdated)) {
                data.add(
                    new LinkData(
                        url,
                        event.getLastUpdated(),
                        host,
                        Map.of(
                            type, event.getType() == null ? "" : event.getType(),
                            ref, event.getReference() == null ? "" : event.getReference(),
                            refType, event.getReferenceType() == null ? "" : event.getReferenceType(),
                            commitsCount, String.valueOf(
                                event.getCommitsCounter() == null ? "" : event.getCommitsCounter()
                            )
                        )
                    )
                );
            }
        }
        return data.reversed();
    }

    @Override
    public boolean isUrlSupported(URL url) {
        Matcher matcher = urlPattern.matcher(url.toString());
        return matcher.matches();
    }

    @Getter
    private static class GitHubEvent {
        private final OffsetDateTime lastUpdated;
        private final String type;
        private final String reference;
        private final String referenceType;
        private final Integer commitsCounter;

        GitHubEvent(
            @JsonProperty("created_at") OffsetDateTime lastUpdated,
            @JsonProperty("type") String type,
            @JsonProperty("payload") Map<String, Object> payload
        ) {
            this.lastUpdated = lastUpdated;
            this.type = type;
            this.reference = (String) payload.get("ref");
            this.referenceType = (String) payload.get("ref_type");
            this.commitsCounter = (Integer) payload.get("size");
        }
    }
}
