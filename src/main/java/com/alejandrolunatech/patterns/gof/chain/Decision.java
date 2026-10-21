package com.alejandrolunatech.patterns.gof.chain;

public sealed interface Decision {

    record Approved(String by) implements Decision {
    }

    record Rejected(String by, String reason) implements Decision {
    }

    /** No handler in the chain could decide; a person has to. */
    record Escalated() implements Decision {
    }
}
