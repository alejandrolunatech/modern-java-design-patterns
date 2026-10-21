package com.alejandrolunatech.patterns.gof.observer;

/** Events are immutable records in a sealed hierarchy, so listeners can switch on them safely. */
public sealed interface OrderEvent {

    String orderId();

    record Placed(String orderId, long totalCents) implements OrderEvent {
    }

    record Shipped(String orderId, String trackingCode) implements OrderEvent {
    }
}
