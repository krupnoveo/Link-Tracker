package edu.java.clients;

import java.util.Properties;
import java.util.regex.Pattern;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class BaseClient implements Client {
    protected final WebClient webClient;
    protected final Properties properties;
    protected String host;
    protected Pattern urlPattern;

    public BaseClient(String url, Properties properties) {
        this.webClient = WebClient.create(url);
        this.properties = properties;
    }

    public BaseClient(WebClient webClient, Properties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }
}
