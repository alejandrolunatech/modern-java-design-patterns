package com.alejandrolunatech.patterns.gof.observer;

/** An {@link AutoCloseable} without a checked exception: closing it unsubscribes. */
@FunctionalInterface
public interface Subscription extends AutoCloseable {

    @Override
    void close();
}
