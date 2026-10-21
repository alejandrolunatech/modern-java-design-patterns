package com.alejandrolunatech.patterns.llm.resilience;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Stops calling a failing model for a while, so a vendor outage costs one fast failure per call
 * instead of a timeout per call. After the cool-down, one trial call decides whether to close.
 */
public final class CircuitBreaker {

    public sealed interface State {

        record Closed(int consecutiveFailures) implements State {
        }

        record Open(Instant until) implements State {
        }

        record HalfOpen() implements State {
        }
    }

    private final int failureThreshold;
    private final Duration openFor;
    private final Clock clock;
    private State state = new State.Closed(0);

    public CircuitBreaker(int failureThreshold, Duration openFor, Clock clock) {
        if (failureThreshold < 1) {
            throw new IllegalArgumentException("failureThreshold must be at least 1");
        }
        this.failureThreshold = failureThreshold;
        this.openFor = Objects.requireNonNull(openFor);
        this.clock = Objects.requireNonNull(clock);
    }

    /** True when a call may go through. Moves Open to HalfOpen once the cool-down has passed. */
    public synchronized boolean allowRequest() {
        if (state instanceof State.Open open) {
            if (clock.instant().isBefore(open.until())) {
                return false;
            }
            state = new State.HalfOpen();
        }
        return true;
    }

    public synchronized void recordSuccess() {
        state = new State.Closed(0);
    }

    public synchronized void recordFailure() {
        state = switch (state) {
            case State.Closed closed when closed.consecutiveFailures() + 1 < failureThreshold ->
                    new State.Closed(closed.consecutiveFailures() + 1);
            case State.Closed closed -> open();
            case State.HalfOpen halfOpen -> open();
            case State.Open open -> open;
        };
    }

    private State.Open open() {
        return new State.Open(clock.instant().plus(openFor));
    }

    public synchronized State state() {
        return state;
    }
}
