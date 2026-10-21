package com.alejandrolunatech.patterns.gof.strategy;

import java.util.Objects;

/** The context: it knows that shipping costs something, not how it is calculated. */
public final class Checkout {

    private final ShippingCost shipping;

    public Checkout(ShippingCost shipping) {
        this.shipping = Objects.requireNonNull(shipping);
    }

    public long totalCents(Parcel parcel) {
        return parcel.orderTotalCents() + shipping.centsFor(parcel);
    }
}
