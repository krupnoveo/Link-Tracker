package edu.java.api.httpClient;

import edu.java.api.dto.response.ApiErrorResponse;
import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {
    private final static String BASE_PORT = "8090";
    private final static String BASE_URL = "http://localhost:" + BASE_PORT;
    private final static String PATH_FOR_UPDATE_LINKS_CONTROLLER = "/updates";
    private final WebClient webClient;

    public BotClient() {
        this.webClient = WebClient
            .builder()
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .baseUrl(BASE_URL)
            .build();
    }

    public BotClient(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public GenericResponse<Void> notifyChats(List<LinkUpdate> linkUpdates) {
        var clientResponse = webClient
            .post()
            .uri(PATH_FOR_UPDATE_LINKS_CONTROLLER)
            .bodyValue(linkUpdates)
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(Void.class);
                }
                return response.bodyToMono(ApiErrorResponse.class);
            })
            .block();
        if (clientResponse == null) {
            return new GenericResponse<>(null, null);
        }
        return new GenericResponse<>(null, (ApiErrorResponse) clientResponse);
    }

}
