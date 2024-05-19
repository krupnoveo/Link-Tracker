package edu.java.api.controllers.rateLimit;

import io.github.bucket4j.Bucket;
import java.time.Duration;


public class RateLimiter {
    private final Bucket bucket;

    public RateLimiter(
        Integer maxTokens,
        Integer amountOfTokensGeneratedPerPeriod,
        Duration period
    ) {
        this.bucket = Bucket.builder()
            .addLimit(limit ->
                limit.capacity(maxTokens).refillGreedy(amountOfTokensGeneratedPerPeriod, period))
            .build();
    }

    public boolean tryConsumeToken() {
        return bucket.tryConsume(1);
    }
}
