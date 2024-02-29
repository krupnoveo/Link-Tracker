package edu.java.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.dto.LinkData;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubClient extends BaseClient {

    private final static String BASE_URL = "https://api.github.com";
    private final static String HOST = "github.com";
    private final static Pattern URL_PATTERN = Pattern.compile("https://" + HOST + "/[\\w-]+/[\\w-]+");


    public GitHubClient() {
        this(BASE_URL);
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
