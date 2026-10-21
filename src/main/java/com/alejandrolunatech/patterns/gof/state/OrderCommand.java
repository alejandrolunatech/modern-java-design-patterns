package com.alejandrolunatech.patterns.gof.state;

import java.time.Instant;

public sealed interface OrderCommand {

    record Place(Instant at) implements OrderCommand {
    }

    record Pay(String paymentReference) implements OrderCommand {
    }

    record Ship(String trackingCode) implements OrderCommand {
    }

    record Cancel(String reason) implements OrderCommand {
    }
}
