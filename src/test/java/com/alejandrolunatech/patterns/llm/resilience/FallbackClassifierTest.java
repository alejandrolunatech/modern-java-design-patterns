package com.alejandrolunatech.patterns.llm.resilience;

import static org.assertj.core.api.Assertions.assertThat;

import com.alejandrolunatech.patterns.llm.core.Classification;
import com.alejandrolunatech.patterns.llm.core.Classifier;
import com.alejandrolunatech.patterns.llm.core.ModelUnavailableException;
import com.alejandrolunatech.patterns.llm.core.Verdict;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FallbackClassifierTest {

    private final KeywordClassifier rules = new KeywordClassifier(Map.of(
            "REFUND", List.of("refund", "money back"),
            "DELIVERY", List.of("parcel", "delivery")));

    @Test
    void usesThePrimaryWhenItAnswers() {
        Verdict fromModel = new Verdict.Accepted(new Classification("ACCOUNT", 0.9, Classification.Source.MODEL));
        Classifier classifier = new FallbackClassifier(text -> fromModel, rules);

        assertThat(classifier.classify("I want my money back")).isEqualTo(fromModel);
    }

    @Test
    void fallsBackToRulesWhenTheModelIsDown() {
        Classifier down = text -> {
            throw new ModelUnavailableException("outage");
        };
        Classifier classifier = new FallbackClassifier(down, rules);

        assertThat(classifier.classify("I want my money back")).isEqualTo(
                new Verdict.Accepted(new Classification("REFUND", 1.0, Classification.Source.RULES)));
        assertThat(classifier.classify("Hello?")).isEqualTo(new Verdict.NeedsReview("no rule matched"));
    }
}
