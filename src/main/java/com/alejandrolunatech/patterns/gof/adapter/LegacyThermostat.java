package com.alejandrolunatech.patterns.gof.adapter;

/**
 * The adaptee: an old device client we cannot change. It answers with a small XML fragment such
 * as {@code <reading unit="F">71.6</reading>}.
 */
public interface LegacyThermostat {

    String currentReadingXml();
}
