package com.alejandrolunatech.patterns.gof.state;

import com.alejandrolunatech.patterns.gof.state.OrderCommand.Cancel;
import com.alejandrolunatech.patterns.gof.state.OrderCommand.Pay;
import com.alejandrolunatech.patterns.gof.state.OrderCommand.Place;
import com.alejandrolunatech.patterns.gof.state.OrderCommand.Ship;
import com.alejandrolunatech.patterns.gof.state.OrderState.Cancelled;
import com.alejandrolunatech.patterns.gof.state.OrderState.Draft;
import com.alejandrolunatech.patterns.gof.state.OrderState.Paid;
import com.alejandrolunatech.patterns.gof.state.OrderState.Placed;
import com.alejandrolunatech.patterns.gof.state.OrderState.Shipped;

/**
 * The whole lifecycle in one pure function: (state, command) to the next state. Every legal
 * transition is listed here; anything else is refused. The classic State pattern spreads the
 * same rules across one class per state.
 */
public final class OrderStateMachine {

    private OrderStateMachine() {
    }

    public static OrderState next(OrderState state, OrderCommand command) {
        return switch (state) {
            case Draft draft -> switch (command) {
                case Place place -> new Placed(place.at());
                case Cancel cancel -> new Cancelled(cancel.reason());
                default -> throw illegal(state, command);
            };
            case Placed placed -> switch (command) {
                case Pay pay -> new Paid(placed.placedAt(), pay.paymentReference());
                case Cancel cancel -> new Cancelled(cancel.reason());
                default -> throw illegal(state, command);
            };
            case Paid paid -> switch (command) {
                case Ship ship -> new Shipped(paid.paymentReference(), ship.trackingCode());
                default -> throw illegal(state, command);
            };
            case Shipped shipped -> throw illegal(state, command);
            case Cancelled cancelled -> throw illegal(state, command);
        };
    }

    private static IllegalStateTransitionException illegal(OrderState state, OrderCommand command) {
        return new IllegalStateTransitionException(state, command);
    }

    public static final class IllegalStateTransitionException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        IllegalStateTransitionException(OrderState state, OrderCommand command) {
            super("cannot " + command.getClass().getSimpleName() + " an order in state "
                    + state.getClass().getSimpleName());
        }
    }
}
