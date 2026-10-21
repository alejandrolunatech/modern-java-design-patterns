package com.alejandrolunatech.patterns.gof.observer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Observers are plain {@link Consumer}s. Subscribing returns a handle that unsubscribes, so
 * listeners can be used with try-with-resources and never leak.
 *
 * <p>A failing listener does not stop the others: its exception goes to the error handler.
 */
public final class EventBus<E> {

    private final List<Consumer<? super E>> listeners = new CopyOnWriteArrayList<>();
    private final BiConsumer<? super E, RuntimeException> onListenerError;

    public EventBus(BiConsumer<? super E, RuntimeException> onListenerError) {
        this.onListenerError = Objects.requireNonNull(onListenerError);
    }

    public Subscription subscribe(Consumer<? super E> listener) {
        Objects.requireNonNull(listener);
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    /** Returns how many listeners handled the event without throwing. */
    public int publish(E event) {
        int delivered = 0;
        for (Consumer<? super E> listener : listeners) {
            try {
                listener.accept(event);
                delivered++;
            } catch (RuntimeException e) {
                onListenerError.accept(event, e);
            }
        }
        return delivered;
    }

    public int listenerCount() {
        return listeners.size();
    }
}
