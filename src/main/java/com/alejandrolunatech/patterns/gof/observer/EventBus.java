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
 * <p>A failing listener does not stop the others: its exception goes to the error handler. If
 * the error handler itself throws, that is dropped too, so delivery always reaches every listener.
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
                report(event, e);
            }
        }
        return delivered;
    }

    private void report(E event, RuntimeException error) {
        try {
            onListenerError.accept(event, error);
        } catch (RuntimeException handlerFailure) {
            // The handler was the last place to report to; the remaining listeners matter more.
        }
    }

    public int listenerCount() {
        return listeners.size();
    }
}
