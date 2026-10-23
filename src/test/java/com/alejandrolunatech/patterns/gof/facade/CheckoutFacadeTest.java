package com.alejandrolunatech.patterns.gof.facade;

import static org.assertj.core.api.Assertions.assertThat;

import com.alejandrolunatech.patterns.gof.facade.CheckoutFacade.OrderRequest;
import com.alejandrolunatech.patterns.gof.facade.CheckoutFacade.Result;
import com.alejandrolunatech.patterns.gof.facade.Subsystems.Inventory;
import com.alejandrolunatech.patterns.gof.facade.Subsystems.OutOfStockException;
import com.alejandrolunatech.patterns.gof.facade.Subsystems.PaymentDeclinedException;
import com.alejandrolunatech.patterns.gof.facade.Subsystems.Payments;
import com.alejandrolunatech.patterns.gof.facade.Subsystems.ShippingUnavailableException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CheckoutFacadeTest {

    private final OrderRequest request = new OrderRequest("c-1", "SKU-1", 2, 4_990, "Poolsterstraat 1, Hoorn");
    private final FakeInventory inventory = new FakeInventory();
    private final FakePayments payments = new FakePayments();

    @Test
    void placesAnOrderInOneCall() {
        CheckoutFacade facade = new CheckoutFacade(inventory, payments, (reservation, address) -> "3S123");

        assertThat(facade.placeOrder(request)).isEqualTo(new Result.Placed("pay-1", "3S123"));
        assertThat(inventory.reserved).containsExactly("res-SKU-1");
    }

    @Test
    void releasesTheReservationWhenPaymentIsDeclined() {
        payments.declineWith = "insufficient funds";
        CheckoutFacade facade = new CheckoutFacade(inventory, payments, (reservation, address) -> "never");

        assertThat(facade.placeOrder(request)).isEqualTo(new Result.Rejected("payment declined: insufficient funds"));
        assertThat(inventory.reserved).isEmpty();
    }

    @Test
    void rejectsWithoutChargingWhenOutOfStock() {
        inventory.outOfStock = true;
        CheckoutFacade facade = new CheckoutFacade(inventory, payments, (reservation, address) -> "never");

        assertThat(facade.placeOrder(request)).isEqualTo(new Result.Rejected("out of stock: SKU-1"));
        assertThat(payments.charged).isEmpty();
    }

    @Test
    void refundsAndReleasesWhenShippingIsUnavailable() {
        CheckoutFacade facade = new CheckoutFacade(inventory, payments, (reservation, address) -> {
            throw new ShippingUnavailableException("no carrier for this address");
        });

        assertThat(facade.placeOrder(request))
                .isEqualTo(new Result.Rejected("shipping unavailable: no carrier for this address"));
        assertThat(payments.charged).isEmpty();
        assertThat(payments.refunded).containsExactly("pay-1");
        assertThat(inventory.reserved).isEmpty();
    }

    static final class FakePayments implements Payments {

        final List<String> charged = new ArrayList<>();
        final List<String> refunded = new ArrayList<>();
        String declineWith;

        @Override
        public String charge(String customerId, long amountCents) {
            if (declineWith != null) {
                throw new PaymentDeclinedException(declineWith);
            }
            String reference = "pay-" + (charged.size() + refunded.size() + 1);
            charged.add(reference);
            return reference;
        }

        @Override
        public void refund(String paymentReference) {
            charged.remove(paymentReference);
            refunded.add(paymentReference);
        }
    }

    static final class FakeInventory implements Inventory {

        final Set<String> reserved = new HashSet<>();
        boolean outOfStock;

        @Override
        public String reserve(String sku, int quantity) {
            if (outOfStock) {
                throw new OutOfStockException(sku);
            }
            reserved.add("res-" + sku);
            return "res-" + sku;
        }

        @Override
        public void release(String reservationId) {
            reserved.remove(reservationId);
        }
    }
}
