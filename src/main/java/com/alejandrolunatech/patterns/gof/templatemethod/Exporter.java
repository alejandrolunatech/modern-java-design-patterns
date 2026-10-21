package com.alejandrolunatech.patterns.gof.templatemethod;

import java.util.List;
import java.util.function.Function;

/**
 * The same algorithm with composition: the steps are functions passed in, so a new format is a
 * value, not a subclass, and the steps can be tested and reused one by one.
 */
public record Exporter(String header, Function<Row, String> formatRow, Function<List<Row>, String> footer) {

    public String export(List<Row> rows) {
        StringBuilder out = new StringBuilder(header);
        rows.forEach(row -> out.append(formatRow.apply(row)));
        return out.append(footer.apply(rows)).toString();
    }

    public static Exporter csv() {
        return new Exporter("name,amount\n",
                row -> row.name() + "," + ReportExporter.euros(row.amountCents()) + "\n",
                rows -> "total," + ReportExporter.euros(rows.stream().mapToLong(Row::amountCents).sum()) + "\n");
    }

    public static Exporter markdown() {
        return new Exporter("| Name | Amount |\n| --- | ---: |\n",
                row -> "| " + row.name() + " | " + ReportExporter.euros(row.amountCents()) + " |\n",
                rows -> "");
    }
}
