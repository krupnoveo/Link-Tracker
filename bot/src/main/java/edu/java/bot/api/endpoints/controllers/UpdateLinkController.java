package edu.java.bot.api.endpoints.controllers;

import edu.java.bot.api.dto.request.LinkUpdate;
import edu.java.bot.api.endpoints.controllers.rateLimit.RateLimit;
import edu.java.bot.api.service.LinkUpdatesService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(path = "/updates")
@RequiredArgsConstructor
public class UpdateLinkController {
    private final LinkUpdatesService service;

    @PostMapping
    @Operation(summary = "Отправить обновление")
    @RateLimit
    public void updateLinks(
        @RequestBody @Valid List<LinkUpdate> linkUpdates,
        HttpServletRequest request
    ) {
        service.notifyUsers(linkUpdates);
    }
}
