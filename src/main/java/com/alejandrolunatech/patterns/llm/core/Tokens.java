package com.alejandrolunatech.patterns.llm.core;

/** A rough token count, about four characters per token. Good enough for tests and budgets, never for billing. */
public final class Tokens {

    private Tokens() {
    }

    public static int estimate(String text) {
        return Math.max(1, (text.length() + 3) / 4);
    }
}
