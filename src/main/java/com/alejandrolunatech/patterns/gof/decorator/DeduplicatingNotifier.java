package com.alejandrolunatech.patterns.gof.decorator;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Drops a message that was already sent to the same recipient within the window. The clock is
 * injected, so tests control time instead of sleeping.
 */
public final class DeduplicatingNotifier implements Notifier {

    private record Key(String recipient, String message) {
    }

    private final Notifier inner;
    private final Duration window;
    private final Clock clock;
    private final Map<Key, Instant> lastSent = new HashMap<>();

    public DeduplicatingNotifier(Notifier inner, Duration window, Clock clock) {
        this.inner = Objects.requireNonNull(inner);
        this.window = Objects.requireNonNull(window);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public synchronized void send(String recipient, String message) {
        Key key = new Key(recipient, message);
        Instant now = clock.instant();
        Instant previous = lastSent.get(key);
        if (previous != null && previous.plus(window).isAfter(now)) {
            return;
        }
        inner.send(recipient, message);
        lastSent.put(key, now);
    }
}
