package com.alejandrolunatech.patterns.llm.core;

/** A label for a customer message, how sure we are, and who decided: the model or the rules. */
public record Classification(String label, double confidence, Source source) {

    public enum Source { MODEL, RULES }
}
