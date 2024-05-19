package edu.java.bot.util;

import edu.java.bot.configuration.RetryConfiguration.Client;
import edu.java.bot.configuration.RetryConfiguration.RetryMode;
import edu.java.bot.configuration.RetryConfiguration.RetrySpecification;
import edu.java.bot.models.RetryRule;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

@UtilityClass
public class RetryFactory {
    private final Map<RetryMode, Function<RetrySpecification, RetryRule>> retryRules = Map.of(
        RetryMode.FIXED, retrySpec -> new RetryRule(
            RetryBackoffSpec
                .fixedDelay(
                    retrySpec.maxAttempts(),
                    retrySpec.minDelay()
                ),
            retrySpec.codes()
        ),
        RetryMode.LINEAR, retrySpec -> new RetryRule(
            LinearRetryBackoffSpec
                .linear(
                    retrySpec.maxAttempts(),
                    retrySpec.minDelay()
                )
                .multiplier(retrySpec.multiplier()),
            retrySpec.codes()
        ),
        RetryMode.EXPONENTIAL, retrySpec -> new RetryRule(
            RetryBackoffSpec
                .backoff(
                    retrySpec.maxAttempts(),
                    retrySpec.minDelay()
                )
                .maxBackoff(retrySpec.maxDelay()),
            retrySpec.codes()
        )
    );

    public static ExchangeFilterFunction createFilter(RetryRule retry) {
        return (response, next) -> next.exchange(response)
            .flatMap(clientResponse -> {
                if (retry.codes().contains(clientResponse.statusCode().value())) {
                    return clientResponse.createError();
                } else {
                    return Mono.just(clientResponse);
                }
            }).retryWhen(retry.rule());
    }

    public static RetryRule createRule(List<RetrySpecification> specificationList, Client client) {
        return specificationList.stream()
            .filter(a -> a.client().equals(client))
            .findFirst()
            .map(a -> retryRules.get(a.mode()).apply(a))
            .orElse(
                new RetryRule(
                    RetryBackoffSpec.fixedDelay(1, Duration.ZERO),
                    List.of()
                )
            );
    }
}
