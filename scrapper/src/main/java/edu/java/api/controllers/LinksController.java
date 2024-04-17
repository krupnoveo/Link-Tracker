package edu.java.api.controllers;

import edu.java.api.dto.request.AddLinkRequest;
import edu.java.api.dto.request.RemoveLinkRequest;
import edu.java.api.dto.response.LinkResponse;
import edu.java.api.dto.response.ListLinksResponse;
import edu.java.api.services.LinksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ListLinksResponse getTrackedLinks(@RequestHeader(REQUEST_HEADER_NAME) long chatId) {
        return service.getTrackedLinks(chatId);
    }

    @PostMapping
    @Operation(summary = "Добавить отслеживание ссылки")
    public LinkResponse addLinkToTracking(
        @RequestHeader(REQUEST_HEADER_NAME) long chatId,
        @RequestBody AddLinkRequest addLinkRequest
        ) {
        return service.addLinkToTracking(chatId, addLinkRequest);
    }

    @DeleteMapping
    @Operation(summary = "Убрать отслеживание ссылки")
    public LinkResponse removeLinkFromTracking(
        @RequestHeader(REQUEST_HEADER_NAME) long chatId,
        @RequestBody RemoveLinkRequest removeLinkRequest
    ) {
        return service.removeLinkFromTracking(chatId, removeLinkRequest);
    }
}
