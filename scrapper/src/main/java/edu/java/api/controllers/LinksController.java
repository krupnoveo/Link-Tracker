package edu.java.api.controllers;

import edu.java.api.controllers.rateLimit.RateLimit;
import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import edu.java.api.services.LinksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Отслеживаемые ссылки", description = "Управление добавлением и удалением ссылок")
@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinksController {
    private final LinksService service;
    private final static String REQUEST_HEADER_NAME = "Tg-Chat-Id";

    @GetMapping
    @Operation(summary = "Получить все отслеживаемые ссылки")
    @RateLimit
    public ListLinksResponse getTrackedLinks(
        @RequestHeader(REQUEST_HEADER_NAME) long chatId,
        HttpServletRequest request
    ) {
        return service.getTrackedLinks(chatId);
    }

    @PostMapping
    @Operation(summary = "Добавить отслеживание ссылки")
    @RateLimit
    public LinkResponse addLinkToTracking(
        @RequestHeader(REQUEST_HEADER_NAME) long chatId,
        @RequestBody AddLinkRequest addLinkRequest,
        HttpServletRequest request
        ) {
        return service.addLinkToTracking(chatId, addLinkRequest);
    }

    @DeleteMapping
    @Operation(summary = "Убрать отслеживание ссылки")
    @RateLimit
    public LinkResponse removeLinkFromTracking(
        @RequestHeader(REQUEST_HEADER_NAME) long chatId,
        @RequestBody RemoveLinkRequest removeLinkRequest,
        HttpServletRequest request
    ) {
        return service.removeLinkFromTracking(chatId, removeLinkRequest);
    }
}
