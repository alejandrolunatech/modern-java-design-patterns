package com.alejandrolunatech.patterns.gof.factory;

/** Behaviour chosen by type with a pattern-matching switch instead of a class hierarchy. */
public final class Fees {

    private Fees() {
    }

    /**
     * There is no {@code default} branch on purpose: add a new payment method and this stops
     * compiling until someone decides its fee.
     */
    public static long feeCents(PaymentMethod method, long amountCents) {
        return switch (method) {
            case PaymentMethod.Card card -> 25 + Math.round(amountCents * 0.014);
            case PaymentMethod.Ideal ideal -> 29;
            case PaymentMethod.BankTransfer transfer -> 0;
        };
    }
}
