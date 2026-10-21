package com.alejandrolunatech.patterns.llm.guardrails;

import static org.assertj.core.api.Assertions.assertThat;

import com.alejandrolunatech.patterns.llm.core.Classification;
import org.junit.jupiter.api.Test;

class ContractParserTest {

    @Test
    void acceptsExactlyTheContract() {
        assertThat(ContractParser.parse("label=REFUND\nconfidence=0.91\n"))
                .contains(new Classification("REFUND", 0.91, Classification.Source.MODEL));
    }

    @Test
    void rejectsEverythingElse() {
        assertThat(ContractParser.parse("Sure! The label is REFUND.")).isEmpty();
        assertThat(ContractParser.parse("label=REFUND")).isEmpty();
        assertThat(ContractParser.parse("label=REFUND\nconfidence=high")).isEmpty();
        assertThat(ContractParser.parse("label=REFUND\nconfidence=1.7")).isEmpty();
        assertThat(ContractParser.parse("label=REFUND\nlabel=OTHER")).isEmpty();
        assertThat(ContractParser.parse("label=REFUND\nconfidence=0.9\nnote=trust me")).isEmpty();
    }
}
