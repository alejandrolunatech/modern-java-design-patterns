package com.alejandrolunatech.patterns.gof.templatemethod;

import java.util.List;

/**
 * The classic Template Method: the algorithm is fixed in a final method, subclasses fill in the
 * steps. Compare it with {@link Exporter}, which does the same with composition.
 */
public abstract class ReportExporter {

    public final String export(List<Row> rows) {
        StringBuilder out = new StringBuilder(header());
        for (Row row : rows) {
            out.append(formatRow(row));
        }
        return out.append(footer(rows)).toString();
    }

    protected abstract String header();

    protected abstract String formatRow(Row row);

    /** A hook with a default: subclasses may override it, but don't have to. */
    protected String footer(List<Row> rows) {
        return "";
    }

    static String euros(long cents) {
        long abs = Math.abs(cents);
        return "%s%d.%02d".formatted(cents < 0 ? "-" : "", abs / 100, abs % 100);
    }
}
