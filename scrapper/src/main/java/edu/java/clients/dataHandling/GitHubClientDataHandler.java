package edu.java.clients.dataHandling;

import edu.java.models.GitHubEvents;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitHubClientDataHandler implements ClientDataHandler {
    private static final String PREFIX = "github.parameter.";
    private final String host;

    private final Properties properties;
    private final GitHubEvents.EventsHolder eventsHolder;

    @Autowired
    public GitHubClientDataHandler(Properties properties, GitHubEvents.EventsHolder eventsHolder) {
        this.properties = properties;
        this.host = properties.getProperty("github.host");
        this.eventsHolder = eventsHolder;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public String getMessageByDescription(Map<String, String> description) {
        if (description != null) {
            String type = description.get(properties.getProperty(PREFIX + "type.name"));
            String ref = description.get(properties.getProperty(PREFIX + "ref.name"));
            String refType = description.get(properties.getProperty(PREFIX + "refType.name"));
            String commitsCount = description.get(properties.getProperty(PREFIX + "commitsCount.name"));
            for (GitHubEvents event : eventsHolder.getEvents()) {
                if (type.equals(event.getEventName())) {
                    return event.getMessageForUpdate(ref, refType, commitsCount);
                }
            }
        }
        return properties.getProperty("github.event.default.updateMessage");
    }

    @Override
    public boolean isHostSupported(String host) {
        return host.equals(this.host);
    }
}
