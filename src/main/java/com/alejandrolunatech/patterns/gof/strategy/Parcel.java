package com.alejandrolunatech.patterns.gof.strategy;

/** What a shipping rule needs to know about a parcel. Amounts are in euro cents. */
public record Parcel(int grams, long orderTotalCents) {

    public Parcel {
        if (grams <= 0) {
            throw new IllegalArgumentException("grams must be positive");
        }
        if (orderTotalCents < 0) {
            throw new IllegalArgumentException("orderTotalCents must not be negative");
        }
    }
}
