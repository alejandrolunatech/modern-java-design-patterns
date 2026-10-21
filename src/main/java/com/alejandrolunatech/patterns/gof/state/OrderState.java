package com.alejandrolunatech.patterns.gof.state;

import java.time.Instant;

/** Each state is a record that carries only the data valid in that state. */
public sealed interface OrderState {

    record Draft() implements OrderState {
    }

    record Placed(Instant placedAt) implements OrderState {
    }

    record Paid(Instant placedAt, String paymentReference) implements OrderState {
    }

    record Shipped(String paymentReference, String trackingCode) implements OrderState {
    }

    record Cancelled(String reason) implements OrderState {
    }
}
