package com.alejandrolunatech.patterns.llm.routing;

import java.math.BigDecimal;

/**
 * What the router knows about a model. {@code quality} is our own score from 1 to 10, taken
 * from evals, not from the vendor's marketing.
 */
public record ModelProfile(String name, int quality, BigDecimal usdPerMillionTokens) {

    public BigDecimal costUsd(int tokens) {
        return usdPerMillionTokens.multiply(BigDecimal.valueOf(tokens)).divide(BigDecimal.valueOf(1_000_000));
    }
}
