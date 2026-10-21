package com.alejandrolunatech.patterns.gof.templatemethod;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExporterTest {

    private final List<Row> rows = List.of(new Row("Hosting", 2_000), new Row("Domain", 1_299));

    @Test
    void theTemplateMethodFixesTheOrderOfSteps() {
        assertThat(new CsvReportExporter().export(rows)).isEqualTo("""
                name,amount
                Hosting,20.00
                Domain,12.99
                total,32.99
                """);
    }

    @Test
    void compositionProducesTheSameOutputWithoutASubclass() {
        assertThat(Exporter.csv().export(rows)).isEqualTo(new CsvReportExporter().export(rows));
    }

    @Test
    void aNewFormatIsJustANewValue() {
        assertThat(Exporter.markdown().export(rows)).isEqualTo("""
                | Name | Amount |
                | --- | ---: |
                | Hosting | 20.00 |
                | Domain | 12.99 |
                """);
    }

    @Test
    void negativeAmountsKeepTheirSign() {
        assertThat(ReportExporter.euros(-50)).isEqualTo("-0.50");
    }
}
