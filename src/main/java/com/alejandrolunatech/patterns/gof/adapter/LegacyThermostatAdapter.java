package com.alejandrolunatech.patterns.gof.adapter;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Translates the legacy format into the target interface, in one place. */
public final class LegacyThermostatAdapter implements TemperatureSensor {

    private static final Pattern READING =
            Pattern.compile("<reading unit=\"([CF])\">(-?\\d+(?:\\.\\d+)?)</reading>");

    private final LegacyThermostat legacy;

    public LegacyThermostatAdapter(LegacyThermostat legacy) {
        this.legacy = Objects.requireNonNull(legacy);
    }

    @Override
    public Temperature read() {
        String xml = legacy.currentReadingXml();
        Matcher matcher = READING.matcher(xml.strip());
        if (!matcher.matches()) {
            throw new IllegalStateException("unexpected reading: " + xml);
        }
        double value = Double.parseDouble(matcher.group(2));
        double celsius = matcher.group(1).equals("F") ? (value - 32) * 5 / 9 : value;
        return new Temperature(celsius);
    }
}
