package com.alejandrolunatech.patterns.gof.strategy;

/**
 * One shipping cost rule. It is a functional interface, so a strategy can be a lambda
 * instead of a class per rule.
 */
@FunctionalInterface
public interface ShippingCost {

    long centsFor(Parcel parcel);

    static ShippingCost flat(long cents) {
        return parcel -> cents;
    }

    /** Charges per started kilogram, so 1,001 g costs two kilograms. */
    static ShippingCost perStartedKilogram(long centsPerKilogram) {
        return parcel -> ((parcel.grams() + 999) / 1000) * centsPerKilogram;
    }

    /** Strategies compose: free above a threshold, otherwise whatever the other rule says. */
    static ShippingCost freeAbove(long orderTotalCents, ShippingCost otherwise) {
        return parcel -> parcel.orderTotalCents() >= orderTotalCents ? 0 : otherwise.centsFor(parcel);
    }
}
