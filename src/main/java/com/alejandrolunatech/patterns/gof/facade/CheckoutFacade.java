package com.alejandrolunatech.patterns.gof.facade;

import com.alejandrolunatech.patterns.gof.facade.Subsystems.Inventory;
import com.alejandrolunatech.patterns.gof.facade.Subsystems.OutOfStockException;
import com.alejandrolunatech.patterns.gof.facade.Subsystems.PaymentDeclinedException;
import com.alejandrolunatech.patterns.gof.facade.Subsystems.Payments;
import com.alejandrolunatech.patterns.gof.facade.Subsystems.Shipping;
import java.util.Objects;

/**
 * One call for the caller; the order of steps and the compensation on failure live here.
 * The facade adds no business rules of its own.
 */
public final class CheckoutFacade {

    public record OrderRequest(String customerId, String sku, int quantity, long amountCents, String address) {
    }

    public sealed interface Result {

        record Placed(String paymentReference, String trackingCode) implements Result {
        }

        record Rejected(String reason) implements Result {
        }
    }

    private final Inventory inventory;
    private final Payments payments;
    private final Shipping shipping;

    public CheckoutFacade(Inventory inventory, Payments payments, Shipping shipping) {
        this.inventory = Objects.requireNonNull(inventory);
        this.payments = Objects.requireNonNull(payments);
        this.shipping = Objects.requireNonNull(shipping);
    }

    public Result placeOrder(OrderRequest request) {
        String reservation;
        try {
            reservation = inventory.reserve(request.sku(), request.quantity());
        } catch (OutOfStockException e) {
            return new Result.Rejected(e.getMessage());
        }
        String payment;
        try {
            payment = payments.charge(request.customerId(), request.amountCents());
        } catch (PaymentDeclinedException e) {
            inventory.release(reservation);
            return new Result.Rejected("payment declined: " + e.getMessage());
        }
        String tracking = shipping.book(reservation, request.address());
        return new Result.Placed(payment, tracking);
    }
}
