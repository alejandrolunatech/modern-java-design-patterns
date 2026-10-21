package com.alejandrolunatech.patterns.gof.decorator;

import java.util.Objects;

/** Removes control characters and cuts long messages before they reach the real notifier. */
public final class SanitizingNotifier implements Notifier {

    private final Notifier inner;
    private final int maxLength;

    public SanitizingNotifier(Notifier inner, int maxLength) {
        if (maxLength < 2) {
            throw new IllegalArgumentException("maxLength must be at least 2");
        }
        this.inner = Objects.requireNonNull(inner);
        this.maxLength = maxLength;
    }

    @Override
    public void send(String recipient, String message) {
        String clean = message.replaceAll("\\p{Cntrl}", " ").strip();
        if (clean.length() > maxLength) {
            clean = clean.substring(0, maxLength - 1) + "…";
        }
        inner.send(recipient, clean);
    }
}
