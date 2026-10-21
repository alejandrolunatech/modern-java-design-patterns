package com.alejandrolunatech.patterns.llm.guardrails;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PiiMaskerTest {

    private final PiiMasker masker = new PiiMasker();

    @Test
    void masksEmailsIbansAndCardNumbers() {
        String text = "I'm ana.de.vries+shop@example.nl, paid with 4111 1111 1111 1111 "
                + "and want the refund on NL91 ABNA 0417 1643 00 or NL91ABNA0417164300.";

        assertThat(masker.apply(text)).isEqualTo(
                "I'm [EMAIL], paid with [CARD] and want the refund on [IBAN] or [IBAN].");
    }

    @Test
    void leavesOrdinaryNumbersAlone() {
        String text = "Order 123456 arrived on 21-10-2026, 3 items.";

        assertThat(masker.apply(text)).isEqualTo(text);
    }
}
