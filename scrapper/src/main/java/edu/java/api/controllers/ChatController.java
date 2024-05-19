package edu.java.api.controllers;

import edu.java.api.services.ChatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Чат", description = "Управление удалением и регистрацией чата")
@RestController
@RequestMapping(path = "/tg-chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatsService service;

    @PostMapping(path = "/{id}")
    @Operation(summary = "Зарегистрировать чат")
    public void registerChat(@PathVariable("id") long chatId) {
        service.registerChat(chatId);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Удалить чат")
    public void deleteChat(@PathVariable("id") long chatId) {
        service.deleteChat(chatId);
    }
}
