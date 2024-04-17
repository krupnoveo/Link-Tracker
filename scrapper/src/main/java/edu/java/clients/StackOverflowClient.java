package edu.java.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.models.LinkData;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackOverflowClient extends BaseClient {
    private final static String API_VERSION = "2.3";
    private final static String BASE_URL = "https://api.stackexchange.com/" + API_VERSION;
    private final static String HOST = "stackoverflow.com";
    private final static Pattern URL_PATTERN = Pattern.compile("https://" + HOST + "/questions/\\d+/[\\w-]+");

    public StackOverflowClient(String url) {
        super(url);
    }

    @Override
    public LinkData checkURL(URL url) {
        String path = url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1);
        StackOverflowResponse response = webClient
            .get()
            .uri(path + "?site=stackoverflow")
            .retrieve()
            .bodyToMono(StackOverflowResponse.class)
            .onErrorComplete()
            .block();
        if (response == null || response.entities() == null || response.entities()[0].lastUpdated() == null) {
            return new LinkData(null, null);
        }
        return new LinkData(url, response.entities()[0].lastUpdated());
    }

    @Override
    public boolean isUrlSupported(URL url) {
        Matcher matcher = URL_PATTERN.matcher(url.toString());
        return matcher.matches();
    }

    private record StackOverflowResponse(
        @JsonProperty("items") StackOverflowItem[] entities
    ) {}

    private record StackOverflowItem(
        @JsonProperty("last_activity_date") OffsetDateTime lastUpdated
    ) {}
}
