package com.alejandrolunatech.patterns.llm.routing;

import java.math.BigDecimal;

/** What a call needs: a minimum quality, its expected size, and the most it may cost. */
public record Task(int minQuality, int estimatedTokens, BigDecimal maxCostUsd) {
}
