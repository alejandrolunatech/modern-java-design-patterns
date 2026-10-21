package com.alejandrolunatech.patterns.gof.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alejandrolunatech.patterns.gof.state.OrderStateMachine.IllegalStateTransitionException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OrderStateMachineTest {

    private final Instant placedAt = Instant.parse("2026-10-21T10:00:00Z");

    @Test
    void theHappyPathEndsShipped() {
        OrderState state = new OrderState.Draft();
        state = OrderStateMachine.next(state, new OrderCommand.Place(placedAt));
        state = OrderStateMachine.next(state, new OrderCommand.Pay("pay-9"));
        state = OrderStateMachine.next(state, new OrderCommand.Ship("3S42"));

        assertThat(state).isEqualTo(new OrderState.Shipped("pay-9", "3S42"));
    }

    @Test
    void anOrderCanBeCancelledBeforePayment() {
        OrderState placed = new OrderState.Placed(placedAt);

        assertThat(OrderStateMachine.next(placed, new OrderCommand.Cancel("changed mind")))
                .isEqualTo(new OrderState.Cancelled("changed mind"));
    }

    @Test
    void illegalTransitionsAreRefused() {
        assertThatThrownBy(() -> OrderStateMachine.next(new OrderState.Draft(), new OrderCommand.Ship("3S42")))
                .isInstanceOf(IllegalStateTransitionException.class)
                .hasMessage("cannot Ship an order in state Draft");
        assertThatThrownBy(() -> OrderStateMachine.next(new OrderState.Paid(placedAt, "pay-9"),
                new OrderCommand.Cancel("too late"))).isInstanceOf(IllegalStateTransitionException.class);
        assertThatThrownBy(() -> OrderStateMachine.next(new OrderState.Shipped("pay-9", "3S42"),
                new OrderCommand.Pay("again"))).isInstanceOf(IllegalStateTransitionException.class);
    }
}
