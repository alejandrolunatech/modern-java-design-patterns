package com.alejandrolunatech.patterns.llm.decorator;

import com.alejandrolunatech.patterns.llm.core.Completion;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.ModelUnavailableException;
import com.alejandrolunatech.patterns.llm.core.Prompt;
import java.time.Duration;
import java.util.Objects;

/**
 * Retries only when the model was unavailable, with exponential backoff. A wrong answer is not
 * retried here; that is a guardrail's decision. The sleeper is injected so tests don't wait.
 */
public final class RetryingModelClient implements ModelClient {

    @FunctionalInterface
    public interface Sleeper {

        void sleep(Duration duration);
    }

    private final ModelClient inner;
    private final int maxAttempts;
    private final Duration firstBackoff;
    private final Sleeper sleeper;

    public RetryingModelClient(ModelClient inner, int maxAttempts, Duration firstBackoff, Sleeper sleeper) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1");
        }
        this.inner = Objects.requireNonNull(inner);
        this.maxAttempts = maxAttempts;
        this.firstBackoff = Objects.requireNonNull(firstBackoff);
        this.sleeper = Objects.requireNonNull(sleeper);
    }

    @Override
    public Completion complete(Prompt prompt) {
        Duration backoff = firstBackoff;
        for (int attempt = 1; ; attempt++) {
            try {
                return inner.complete(prompt);
            } catch (ModelUnavailableException e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                sleeper.sleep(backoff);
                backoff = backoff.multipliedBy(2);
            }
        }
    }
}
