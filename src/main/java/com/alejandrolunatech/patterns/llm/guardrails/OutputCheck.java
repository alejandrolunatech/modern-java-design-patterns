package com.alejandrolunatech.patterns.llm.guardrails;

import com.alejandrolunatech.patterns.llm.core.Classification;
import java.util.Optional;
import java.util.Set;

/**
 * One deterministic check on the model's answer. Returns the problem, or empty to pass. Checks
 * run as a chain: the first problem sends the message to a person.
 */
@FunctionalInterface
public interface OutputCheck {

    Optional<String> problem(Classification classification);

    static OutputCheck allowedLabels(Set<String> labels) {
        return classification -> labels.contains(classification.label())
                ? Optional.empty()
                : Optional.of("unknown label: " + classification.label());
    }

    static OutputCheck minConfidence(double threshold) {
        return classification -> classification.confidence() >= threshold
                ? Optional.empty()
                : Optional.of("confidence %.2f below %.2f".formatted(classification.confidence(), threshold));
    }
}
