package edu.java.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.models.LinkData;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackOverflowClient extends BaseClient {
    private String keyAndToken = "";

    public StackOverflowClient(String url, Properties properties, String key, String token) {
        super(url, properties);
        initializeFields();
        this.keyAndToken = "&access_token=" + token + "&key=" + key;
    }

    public StackOverflowClient(String url, Properties properties) {
        super(url, properties);
        initializeFields();
    }

    private void initializeFields() {
        this.host = properties.getProperty("stackoverflow.host");
        this.urlPattern = Pattern.compile("https://" + host + "/questions/\\d+/[\\w-]+");
    }

    @Override
    public List<LinkData> checkURL(URL url, OffsetDateTime lastUpdated) {
        String path = url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1);
        StackOverflowResponse response = webClient
            .get()
            .uri(path + "?site=stackoverflow" + keyAndToken)
            .retrieve()
            .bodyToMono(StackOverflowResponse.class)
            .onErrorComplete()
            .block();
        if (response == null || response.items() == null || response.items()[0].lastUpdated() == null) {
            return List.of(new LinkData(null, null, host, null));
        }
        return List.of(new LinkData(url, response.items()[0].lastUpdated(), host, null));
    }

    @Override
    public boolean isUrlSupported(URL url) {
        Matcher matcher = urlPattern.matcher(url.toString());
        return matcher.matches();
    }

    private record StackOverflowResponse(
        @JsonProperty("items") StackOverflowItem[] items
    ) {}

    private record StackOverflowItem(
        @JsonProperty("last_activity_date") OffsetDateTime lastUpdated
    ) {}
}
