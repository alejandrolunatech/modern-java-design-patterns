package com.alejandrolunatech.patterns.llm.core;

/** What comes back: the text, which model answered, and the tokens it used. */
public record Completion(String text, String model, int inputTokens, int outputTokens) {
}
