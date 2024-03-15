package edu.java.updatesScheduler;

import edu.java.api.dto.response.ApiErrorResponse;
import edu.java.clientService.ScrapperService;
import edu.java.models.Chat;
import edu.java.models.GenericResponse;
import edu.java.models.LinkDatabaseInformation;
import edu.java.models.LinkUpdate;
import edu.java.updatesScheduler.service.LinkUpdatesSchedulerService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@EnableScheduling
@RequiredArgsConstructor
public class LinkUpdatesScheduler {
    private static final int CHECKED_TIME_IN_MINUTES_CRITERIA = 30;
    private final LinkUpdatesSchedulerService service;
    private final ScrapperService scrapperService;

    @Scheduled(fixedDelayString = "#{@'app-edu.java.configuration.ApplicationConfig'.scheduler.interval}")
    public void update() {
        log.info("updating");
        List<LinkDatabaseInformation> links = service
            .getAllLinksWhichWereNotCheckedForNminutes(
                OffsetDateTime.now().minusMinutes(CHECKED_TIME_IN_MINUTES_CRITERIA)
            );
        List<LinkUpdate> updates = new ArrayList<>();
        for (LinkDatabaseInformation link : links) {
            List<Chat> chats = service.getChatsForLink(link.urlId());
            if (!chats.isEmpty()) {
                OffsetDateTime lastUpdated = service.getUpdatedTimeOfUrl(link.url());
                if (link.lastUpdated() != null && lastUpdated != null && lastUpdated.isAfter(link.lastUpdated())) {
                    service.updateLinkInformationInDatabase(lastUpdated, OffsetDateTime.now(), link.urlId());
                    updates.add(new LinkUpdate(
                            link.urlId(),
                            link.url(),
                            "По ссылке %s произошло обновление!",
                            chats.stream().map(Chat::chatId).toList()
                    ));
                }
            }
        }
        if (!updates.isEmpty()) {
            GenericResponse<Void> response = scrapperService.notifyChats(updates);
            ApiErrorResponse errorResponse = response.errorResponse();
            if (errorResponse != null) {
                log.error(String.join(",", List.of(
                    errorResponse.exceptionName(),
                    errorResponse.code(),
                    errorResponse.exceptionMessage(),
                    errorResponse.description()
                )));
            }
        }
    }
}
