package com.alejandrolunatech.patterns.llm.core;

/**
 * The port every LLM pattern in this package is built around. The application depends on this
 * interface only; vendors, retries, caching and budgets are plugged in around it.
 */
@FunctionalInterface
public interface ModelClient {

    /**
     * @throws ModelUnavailableException when the model cannot answer (timeout, rate limit,
     *     outage). Callers can retry it or fall back.
     */
    Completion complete(Prompt prompt);
}
