package com.alejandrolunatech.patterns.gof.templatemethod;

import java.util.List;

public final class CsvReportExporter extends ReportExporter {

    @Override
    protected String header() {
        return "name,amount\n";
    }

    @Override
    protected String formatRow(Row row) {
        return row.name() + "," + euros(row.amountCents()) + "\n";
    }

    @Override
    protected String footer(List<Row> rows) {
        return "total," + euros(rows.stream().mapToLong(Row::amountCents).sum()) + "\n";
    }
}
