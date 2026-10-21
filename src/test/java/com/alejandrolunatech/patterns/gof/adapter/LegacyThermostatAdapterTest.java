package com.alejandrolunatech.patterns.gof.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

class LegacyThermostatAdapterTest {

    @Test
    void convertsFahrenheitToCelsius() {
        TemperatureSensor sensor = new LegacyThermostatAdapter(() -> "<reading unit=\"F\">71.6</reading>");

        assertThat(sensor.read().celsius()).isCloseTo(22.0, within(1e-9));
    }

    @Test
    void passesCelsiusThrough() {
        TemperatureSensor sensor = new LegacyThermostatAdapter(() -> " <reading unit=\"C\">-3.5</reading>\n");

        assertThat(sensor.read()).isEqualTo(new Temperature(-3.5));
    }

    @Test
    void failsLoudlyOnAFormatItDoesNotKnow() {
        TemperatureSensor sensor = new LegacyThermostatAdapter(() -> "<reading unit=\"K\">295</reading>");

        assertThatThrownBy(sensor::read).hasMessageContaining("unexpected reading");
    }
}
