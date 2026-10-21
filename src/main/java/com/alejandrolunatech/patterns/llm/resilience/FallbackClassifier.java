package com.alejandrolunatech.patterns.llm.resilience;

import com.alejandrolunatech.patterns.llm.core.Classifier;
import com.alejandrolunatech.patterns.llm.core.ModelUnavailableException;
import com.alejandrolunatech.patterns.llm.core.Verdict;
import java.util.Objects;

/** Uses the model path when it can answer and the rules when it can't. */
public final class FallbackClassifier implements Classifier {

    private final Classifier primary;
    private final Classifier fallback;

    public FallbackClassifier(Classifier primary, Classifier fallback) {
        this.primary = Objects.requireNonNull(primary);
        this.fallback = Objects.requireNonNull(fallback);
    }

    @Override
    public Verdict classify(String customerMessage) {
        try {
            return primary.classify(customerMessage);
        } catch (ModelUnavailableException e) {
            return fallback.classify(customerMessage);
        }
    }
}
