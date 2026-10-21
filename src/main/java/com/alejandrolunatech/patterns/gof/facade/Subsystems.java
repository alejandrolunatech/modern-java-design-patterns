package com.alejandrolunatech.patterns.gof.facade;

/** The subsystems the facade coordinates. Each is small and knows nothing of the others. */
public final class Subsystems {

    private Subsystems() {
    }

    public interface Inventory {

        /** Returns a reservation id, or throws {@link OutOfStockException}. */
        String reserve(String sku, int quantity);

        void release(String reservationId);
    }

    public interface Payments {

        /** Returns a payment reference, or throws {@link PaymentDeclinedException}. */
        String charge(String customerId, long amountCents);
    }

    public interface Shipping {

        String book(String reservationId, String address);
    }

    public static final class OutOfStockException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public OutOfStockException(String sku) {
            super("out of stock: " + sku);
        }
    }

    public static final class PaymentDeclinedException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public PaymentDeclinedException(String reason) {
            super(reason);
        }
    }
}
