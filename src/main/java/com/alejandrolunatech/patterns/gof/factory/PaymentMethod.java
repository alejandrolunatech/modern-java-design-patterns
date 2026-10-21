package com.alejandrolunatech.patterns.gof.factory;

import java.util.Locale;

/**
 * A closed set of payment methods. The interface is sealed, so the compiler knows every
 * implementation and can check that a {@code switch} over them is exhaustive.
 */
public sealed interface PaymentMethod {

    /** Only the last four digits are kept: the full card number never enters the domain. */
    record Card(String lastFour) implements PaymentMethod {
        public Card {
            if (!lastFour.matches("\\d{4}")) {
                throw new IllegalArgumentException("lastFour must be four digits");
            }
        }
    }

    record Ideal(String bankCode) implements PaymentMethod {
    }

    record BankTransfer(String iban) implements PaymentMethod {
    }

    /**
     * Static factory: turns text such as {@code "card:4111 1111 1111 1111"} into the right type.
     * Callers depend on the interface, never on the constructors.
     */
    static PaymentMethod parse(String input) {
        int colon = input.indexOf(':');
        if (colon < 0) {
            throw new IllegalArgumentException("expected <kind>:<details>");
        }
        String kind = input.substring(0, colon).trim().toLowerCase(Locale.ROOT);
        String details = input.substring(colon + 1).replaceAll("[\\s-]", "");
        return switch (kind) {
            case "card" -> {
                if (!details.matches("\\d{12,19}")) {
                    throw new IllegalArgumentException("card number must have 12 to 19 digits");
                }
                yield new Card(details.substring(details.length() - 4));
            }
            case "ideal" -> new Ideal(details.toUpperCase(Locale.ROOT));
            case "transfer" -> new BankTransfer(details.toUpperCase(Locale.ROOT));
            default -> throw new IllegalArgumentException("unknown payment method: " + kind);
        };
    }
}
