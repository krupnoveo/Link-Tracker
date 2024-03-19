package edu.java.models;

import java.util.List;
import java.util.Properties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public abstract class GitHubEvents {
    protected final Properties properties;
    @Getter
    protected String eventName;

    public abstract String getMessageForUpdate(String ref, String refType, String commitsCount);

    @Component
    @Getter
    public static class EventsHolder {
        private final List<GitHubEvents> events;

        public EventsHolder(List<GitHubEvents> events) {
            this.events = events;
        }
    }

    @Component
    public static class PushEvent extends GitHubEvents {
        public PushEvent(Properties properties) {
            super(properties);
            this.eventName = properties.getProperty("github.event.push.name");
        }

        @Override
        public String getMessageForUpdate(String ref, String refType, String commitsCount) {
            String branchName = ref.split("/")[2];
            return properties.getProperty("github.event.push.commits.updateMessage")
                .formatted("%s", branchName, commitsCount);
        }
    }

    @Component
    public static class CreateEvent extends GitHubEvents {
        public CreateEvent(Properties properties) {
            super(properties);
            this.eventName = properties.getProperty("github.event.create.name");
        }

        @Override
        public String getMessageForUpdate(String ref, String refType, String commitsCount) {
            if (refType.equals("branch")) {
                return properties.getProperty("github.event.create.branch.updateMessage")
                    .formatted("%s", ref);
            }
            return null;
        }
    }

    @Component
    public static class IssuesEvent extends GitHubEvents {
        public IssuesEvent(Properties properties) {
            super(properties);
            this.eventName = properties.getProperty("github.event.issues.name");
        }

        @Override
        public String getMessageForUpdate(String ref, String refType, String commitsCount) {
            return properties.getProperty("github.event.issues.updateMessage");
        }
    }
}
