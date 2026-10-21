package com.alejandrolunatech.patterns.llm;

import static org.assertj.core.api.Assertions.assertThat;

import com.alejandrolunatech.patterns.llm.core.Classification;
import com.alejandrolunatech.patterns.llm.core.Classifier;
import com.alejandrolunatech.patterns.llm.core.FakeModelClient;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.Verdict;
import com.alejandrolunatech.patterns.llm.decorator.CachingModelClient;
import com.alejandrolunatech.patterns.llm.decorator.CostMeteringModelClient;
import com.alejandrolunatech.patterns.llm.decorator.RetryingModelClient;
import com.alejandrolunatech.patterns.llm.guardrails.GuardedClassifier;
import com.alejandrolunatech.patterns.llm.guardrails.OutputCheck;
import com.alejandrolunatech.patterns.llm.guardrails.PiiMasker;
import com.alejandrolunatech.patterns.llm.resilience.CircuitBreaker;
import com.alejandrolunatech.patterns.llm.resilience.CircuitBreakerModelClient;
import com.alejandrolunatech.patterns.llm.resilience.FallbackClassifier;
import com.alejandrolunatech.patterns.llm.resilience.KeywordClassifier;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * All the LLM patterns wired together, the way a service would compose them. Read it from the
 * inside out: the fake model, then cost, cache, retry, circuit breaker, guardrails, fallback.
 */
class SupportTriageExampleTest {

    private static final Set<String> LABELS = Set.of("REFUND", "DELIVERY", "ACCOUNT", "OTHER");

    private Classifier triage(ModelClient model) {
        ModelClient client = new CostMeteringModelClient(model, new BigDecimal("0.50"), new BigDecimal("5.00"));
        client = new CachingModelClient(client, 1_000);
        client = new RetryingModelClient(client, 2, Duration.ofMillis(100), duration -> { });
        client = new CircuitBreakerModelClient(client, new CircuitBreaker(5, Duration.ofSeconds(30), Clock.systemUTC()));

        Classifier guarded = new GuardedClassifier(client, LABELS, List.of(new PiiMasker()),
                List.of(OutputCheck.allowedLabels(LABELS), OutputCheck.minConfidence(0.7)));
        Classifier rules = new KeywordClassifier(Map.of("REFUND", List.of("refund"), "DELIVERY", List.of("parcel")));
        return new FallbackClassifier(guarded, rules);
    }

    @Test
    void theModelDecidesWhenItIsConfidentAndValid() {
        Verdict verdict = triage(FakeModelClient.replying("label=DELIVERY\nconfidence=0.95"))
                .classify("Where is my parcel? Card 4111 1111 1111 1111.");

        assertThat(verdict).isEqualTo(new Verdict.Accepted(new Classification("DELIVERY", 0.95, Classification.Source.MODEL)));
    }

    @Test
    void theRulesTakeOverDuringAnOutage() {
        Verdict verdict = triage(FakeModelClient.failingFirst(10, "never")).classify("I want a refund");

        assertThat(verdict).isEqualTo(new Verdict.Accepted(new Classification("REFUND", 1.0, Classification.Source.RULES)));
    }

    @Test
    void aPersonGetsWhatNeitherCanDecide() {
        Verdict verdict = triage(FakeModelClient.replying("label=REFUND\nconfidence=0.31")).classify("hmm");

        assertThat(verdict).isInstanceOf(Verdict.NeedsReview.class);
    }
}
