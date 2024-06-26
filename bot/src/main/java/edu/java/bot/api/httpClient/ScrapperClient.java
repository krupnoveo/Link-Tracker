package edu.java.bot.api.httpClient;

import edu.java.bot.api.dto.request.AddLinkRequest;
import edu.java.bot.api.dto.request.RemoveLinkRequest;
import edu.java.bot.api.dto.response.ApiErrorResponse;
import edu.java.bot.models.AddLinkToDatabaseResponse;
import edu.java.bot.models.Chat;
import edu.java.bot.models.GenericResponse;
import edu.java.bot.models.ListLinksResponse;
import edu.java.bot.models.RemoveLinkFromDatabaseResponse;
import edu.java.bot.models.RetryRule;
import edu.java.bot.util.RetryFactory;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
public class ScrapperClient {
    private final static String BASE_PORT = "8080";
    private final static String BASE_URL = "http://localhost:" + BASE_PORT;
    private final static String PATH_FOR_CHAT_CONTROLLER = "/tg-chat/";
    private final static String PATH_FOR_LINKS_CONTROLLER = "/links";
    private final static String HEADER_FOR_LINKS_CONTROLLER = "Tg-Chat-Id";
    private final WebClient webClient;
    private final static ApiErrorResponse EXHAUSTED_RETRY = new ApiErrorResponse(
        "по каким-то причинам сервер временно недоступен, повторите запрос позже",
        HttpStatus.REQUEST_TIMEOUT.toString(),
        "",
        "",
        List.of()
    );

    public ScrapperClient(RetryRule rule) {
        this.webClient = WebClient
            .builder()
            .filter(RetryFactory.createFilter(rule))
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .baseUrl(BASE_URL)
            .build();
    }

    public ScrapperClient(String baseUrl, RetryRule rule) {
        this.webClient = WebClient.create(baseUrl);
    }


    public GenericResponse<Void> registerChat(Chat chat) {
        var clientResponse = webClient
            .method(HttpMethod.POST)
            .uri(PATH_FOR_CHAT_CONTROLLER + chat.id())
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

    public GenericResponse<Void> deleteChat(Chat chat) {
        var clientResponse = webClient
            .method(HttpMethod.DELETE)
            .uri(PATH_FOR_CHAT_CONTROLLER + chat.id())
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

    public GenericResponse<ListLinksResponse> listLinks(long chatId) {
        var clientResponse = webClient
            .method(HttpMethod.GET)
            .uri(PATH_FOR_LINKS_CONTROLLER)
            .header(HEADER_FOR_LINKS_CONTROLLER, String.valueOf(chatId))
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(ListLinksResponse.class);
                }
                return response.bodyToMono(ApiErrorResponse.class);
            })
            .onErrorReturn(throwable -> throwable instanceof IllegalStateException, EXHAUSTED_RETRY)
            .block();
        if (clientResponse instanceof ListLinksResponse) {
            return new GenericResponse<>((ListLinksResponse) clientResponse, null);
        }
        return new GenericResponse<>(null, (ApiErrorResponse) clientResponse);
    }

    public GenericResponse<AddLinkToDatabaseResponse> addLinkToTracking(long chatId, AddLinkRequest addLinkRequest) {
        var clientResponse = webClient
            .method(HttpMethod.POST)
            .uri(PATH_FOR_LINKS_CONTROLLER)
            .header(HEADER_FOR_LINKS_CONTROLLER, String.valueOf(chatId))
            .bodyValue(addLinkRequest)
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(AddLinkToDatabaseResponse.class);
                }
                return response.bodyToMono(ApiErrorResponse.class);
            })
            .onErrorReturn(throwable -> throwable instanceof IllegalStateException, EXHAUSTED_RETRY)
            .block();
        if (clientResponse instanceof AddLinkToDatabaseResponse) {
            return new GenericResponse<>((AddLinkToDatabaseResponse) clientResponse, null);
        }
        return new GenericResponse<>(null, (ApiErrorResponse) clientResponse);
    }

    public GenericResponse<RemoveLinkFromDatabaseResponse> removeLinkFromTracking(
        long chatId,
        RemoveLinkRequest removeLinkRequest
    ) {
        var clientResponse = webClient
            .method(HttpMethod.DELETE)
            .uri(PATH_FOR_LINKS_CONTROLLER)
            .header(HEADER_FOR_LINKS_CONTROLLER, String.valueOf(chatId))
            .bodyValue(removeLinkRequest)
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(RemoveLinkFromDatabaseResponse.class);
                }
                return response.bodyToMono(ApiErrorResponse.class);
            })
            .onErrorReturn(throwable -> throwable instanceof IllegalStateException, EXHAUSTED_RETRY)
            .block();
        if (clientResponse instanceof RemoveLinkFromDatabaseResponse) {
            return new GenericResponse<>((RemoveLinkFromDatabaseResponse) clientResponse, null);
        }
        return new GenericResponse<>(null, (ApiErrorResponse) clientResponse);
    }
}
