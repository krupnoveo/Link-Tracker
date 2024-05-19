package edu.java.api.httpClient;

import edu.java.api.dto.response.ApiErrorResponse;
import edu.java.models.GenericResponse;
import edu.java.models.LinkUpdate;
import edu.java.models.RetryRule;
import edu.java.util.RetryFactory;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {
    private final static String BASE_PORT = "8090";
    private final static String BASE_URL = "http://localhost:" + BASE_PORT;
    private final static String PATH_FOR_UPDATE_LINKS_CONTROLLER = "/updates";
    private final WebClient webClient;
    private final static ApiErrorResponse EXHAUSTED_RETRY = new ApiErrorResponse(
        "по каким-то причинам сервер временно недоступен, повторите запрос позже",
        HttpStatus.REQUEST_TIMEOUT.toString(),
        "",
        "",
        List.of()
    );

    public BotClient(RetryRule rule) {
        this.webClient = WebClient
            .builder()
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .filter(RetryFactory.createFilter(rule))
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
            .onErrorReturn(throwable -> throwable instanceof IllegalStateException, EXHAUSTED_RETRY)
            .block();
        if (clientResponse == null) {
            return new GenericResponse<>(null, null);
        }
        return new GenericResponse<>(null, (ApiErrorResponse) clientResponse);
    }
}
