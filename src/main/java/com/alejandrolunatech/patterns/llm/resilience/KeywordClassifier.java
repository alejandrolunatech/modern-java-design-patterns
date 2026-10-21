package com.alejandrolunatech.patterns.llm.resilience;

import com.alejandrolunatech.patterns.llm.core.Classification;
import com.alejandrolunatech.patterns.llm.core.Classifier;
import com.alejandrolunatech.patterns.llm.core.Verdict;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Plain rules: worse than the model on messy text, but always available and always the same.
 * Rules are checked in the order given; the first label with a matching keyword wins.
 */
public final class KeywordClassifier implements Classifier {

    private final Map<String, List<String>> keywordsByLabel;

    public KeywordClassifier(Map<String, List<String>> keywordsByLabel) {
        this.keywordsByLabel = new LinkedHashMap<>(keywordsByLabel);
    }

    @Override
    public Verdict classify(String customerMessage) {
        String text = customerMessage.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, List<String>> rule : keywordsByLabel.entrySet()) {
            if (rule.getValue().stream().anyMatch(text::contains)) {
                return new Verdict.Accepted(new Classification(rule.getKey(), 1.0, Classification.Source.RULES));
            }
        }
        return new Verdict.NeedsReview("no rule matched");
    }
}
