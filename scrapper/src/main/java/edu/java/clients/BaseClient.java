package edu.java.clients;

import org.springframework.web.reactive.function.client.WebClient;

public abstract class BaseClient implements Client {
    protected WebClient webClient;

    public BaseClient(String url) {
        this.webClient = WebClient.create(url);
    }
}
