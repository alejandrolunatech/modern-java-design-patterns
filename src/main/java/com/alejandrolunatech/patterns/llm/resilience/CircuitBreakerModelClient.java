package com.alejandrolunatech.patterns.llm.resilience;

import com.alejandrolunatech.patterns.llm.core.Completion;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.ModelUnavailableException;
import com.alejandrolunatech.patterns.llm.core.Prompt;
import java.util.Objects;

public final class CircuitBreakerModelClient implements ModelClient {

    private final ModelClient inner;
    private final CircuitBreaker breaker;

    public CircuitBreakerModelClient(ModelClient inner, CircuitBreaker breaker) {
        this.inner = Objects.requireNonNull(inner);
        this.breaker = Objects.requireNonNull(breaker);
    }

    @Override
    public Completion complete(Prompt prompt) {
        if (!breaker.allowRequest()) {
            throw new CircuitOpenException();
        }
        try {
            Completion completion = inner.complete(prompt);
            breaker.recordSuccess();
            return completion;
        } catch (ModelUnavailableException e) {
            breaker.recordFailure();
            throw e;
        }
    }

    /** Still a {@link ModelUnavailableException}, so callers handle both the same way. */
    public static final class CircuitOpenException extends ModelUnavailableException {

        private static final long serialVersionUID = 1L;

        CircuitOpenException() {
            super("circuit open: model calls paused");
        }
    }
}
