package com.alejandrolunatech.patterns.llm.core;

/** The only two outcomes the rest of the system sees. There is no "maybe" that slips through. */
public sealed interface Verdict {

    record Accepted(Classification classification) implements Verdict {
    }

    record NeedsReview(String reason) implements Verdict {
    }
}
