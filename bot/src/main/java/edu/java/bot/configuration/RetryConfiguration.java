package edu.java.bot.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "retry", ignoreUnknownFields = false)
public record RetryConfiguration(List<RetrySpecification> clientConfigs) {
    public record RetrySpecification(
        @NotNull Client client,
        @NotNull RetryMode mode,
        @NotNull Integer maxAttempts,
        Duration minDelay,
        Duration maxDelay,
        Double multiplier,
        List<Integer> codes
    ) {
        public RetrySpecification(
            Client client,
            RetryMode mode,
            Integer maxAttempts,
            Duration minDelay,
            Duration maxDelay,
            Double multiplier,
            List<Integer> codes
        ) {
            this.client = client;
            this.mode = mode;
            this.maxAttempts = maxAttempts;
            this.minDelay = minDelay == null ? Duration.ofSeconds(2) : minDelay;
            this.maxDelay = maxDelay == null ? Duration.ofMinutes(1) : maxDelay;
            this.multiplier = multiplier == null ? 1.0 : multiplier;
            this.codes = codes;
        }
    }

    public enum Client {
        SCRAPPER
    }

    public enum RetryMode {
        FIXED, LINEAR, EXPONENTIAL
    }
}
