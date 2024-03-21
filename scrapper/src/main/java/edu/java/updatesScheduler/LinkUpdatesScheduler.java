package edu.java.updatesScheduler;

import edu.java.api.dto.response.ApiErrorResponse;
import edu.java.httpClientService.ScrapperService;
import edu.java.models.Chat;
import edu.java.models.GenericResponse;
import edu.java.models.LinkData;
import edu.java.models.LinkDatabaseInformation;
import edu.java.models.LinkUpdate;
import edu.java.updatesScheduler.service.LinkUpdatesSchedulerService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private static final int CHECKED_TIME_IN_MINUTES_CRITERIA = 0;
    private final LinkUpdatesSchedulerService service;
    private final ScrapperService scrapperService;

    @Scheduled(fixedDelayString = "#{@'app-edu.java.configuration.ApplicationConfig'.scheduler.interval}")
    public void update() {
        log.info("updating");
        List<LinkDatabaseInformation> links = getLinksFromDatabase();
        List<LinkUpdate> updates = new ArrayList<>();
        for (LinkDatabaseInformation link : links) {
            List<Chat> chats = service.getChatsForLink(link.urlId());
            if (!chats.isEmpty()) {
                List<LinkData> dataList = service.getLinkDataOfUrl(link.url(), link.lastUpdated());
                if (dataList.isEmpty()) {
                    updateLinkInformationInDatabase(link.lastUpdated(), link.urlId());
                }
                for (LinkData data : dataList) {
                    handleLinkData(updates, data, link, chats);
                }
            }
        }
        processUpdates(updates);
    }

    private void handleLinkData(
        List<LinkUpdate> updates,
        LinkData data,
        LinkDatabaseInformation link,
        List<Chat> chats
    ) {
        OffsetDateTime lastUpdated = data.lastUpdated();
        String host = data.host();
        Map<String, String> description = data.description();
        String message = service.getMessageByDescriptionAndHost(host, description);
        if (
            link.lastUpdated() != null
                && lastUpdated != null
                && lastUpdated.isAfter(link.lastUpdated())
        ) {
            updateLinkInformationInDatabase(lastUpdated, link.urlId());
            updates.add(new LinkUpdate(
                link.urlId(),
                link.url(),
                message.formatted(link.url()),
                chats.stream().map(Chat::chatId).toList()
            ));
        } else {
            updateLinkInformationInDatabase(link.lastUpdated(), link.urlId());
        }
    }

    private void updateLinkInformationInDatabase(OffsetDateTime lastUpdated, long urlId) {
        service.updateLinkInformationInDatabase(lastUpdated, OffsetDateTime.now(), urlId);
    }

    private List<LinkDatabaseInformation> getLinksFromDatabase() {
        return service
            .getAllLinksWhichWereNotCheckedForNminutes(
                CHECKED_TIME_IN_MINUTES_CRITERIA
            );
    }

    private void processUpdates(List<LinkUpdate> updates) {
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
