package com.alejandrolunatech.patterns.gof.observer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EventBusTest {

    private final List<String> errors = new ArrayList<>();
    private final EventBus<OrderEvent> bus = new EventBus<>((event, error) -> errors.add(error.getMessage()));

    @Test
    void everyListenerHearsEveryEvent() {
        List<OrderEvent> first = new ArrayList<>();
        List<OrderEvent> second = new ArrayList<>();
        bus.subscribe(first::add);
        bus.subscribe(second::add);

        bus.publish(new OrderEvent.Placed("o-1", 2_500));

        assertThat(first).containsExactly(new OrderEvent.Placed("o-1", 2_500));
        assertThat(second).isEqualTo(first);
    }

    @Test
    void closingTheSubscriptionStopsDelivery() {
        List<OrderEvent> heard = new ArrayList<>();
        Subscription subscription = bus.subscribe(heard::add);
        bus.publish(new OrderEvent.Placed("o-1", 100));
        subscription.close();
        bus.publish(new OrderEvent.Shipped("o-1", "3SABC123"));

        assertThat(heard).hasSize(1);
        assertThat(bus.listenerCount()).isZero();
    }

    @Test
    void oneFailingListenerDoesNotStopTheOthers() {
        List<String> shippedOrders = new ArrayList<>();
        bus.subscribe(event -> {
            throw new IllegalStateException("mail server down");
        });
        bus.subscribe(event -> {
            if (event instanceof OrderEvent.Shipped shipped) {
                shippedOrders.add(shipped.orderId());
            }
        });

        int delivered = bus.publish(new OrderEvent.Shipped("o-7", "3SXYZ"));

        assertThat(delivered).isEqualTo(1);
        assertThat(shippedOrders).containsExactly("o-7");
        assertThat(errors).containsExactly("mail server down");
    }
}
