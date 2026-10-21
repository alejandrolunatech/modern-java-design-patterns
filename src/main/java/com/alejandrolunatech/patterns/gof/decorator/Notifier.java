package com.alejandrolunatech.patterns.gof.decorator;

/** The component every decorator wraps. */
public interface Notifier {

    void send(String recipient, String message);
}
