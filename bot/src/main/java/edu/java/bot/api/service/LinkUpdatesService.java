package edu.java.bot.api.service;

import edu.java.bot.api.dto.request.LinkUpdate;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class LinkUpdatesService {
    public void notifyUsers(LinkUpdate linkUpdate) {
        log.info("Notifying users...");
    }
}
