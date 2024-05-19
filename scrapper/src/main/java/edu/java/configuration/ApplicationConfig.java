package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    AccessType databaseAccessType,
    Kafka kafka,
    Boolean useQueue
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public enum AccessType {
        JDBC, JPA, JOOQ
    }

    public record Kafka(Map<String, String> topics) {}
}
