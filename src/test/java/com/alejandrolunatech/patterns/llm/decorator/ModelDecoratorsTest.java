package com.alejandrolunatech.patterns.llm.decorator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alejandrolunatech.patterns.llm.core.FakeModelClient;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.ModelUnavailableException;
import com.alejandrolunatech.patterns.llm.core.Prompt;
import com.alejandrolunatech.patterns.llm.decorator.CostMeteringModelClient.BudgetExceededException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ModelDecoratorsTest {

    private final Prompt prompt = new Prompt("Answer in one word.", "Capital of the Netherlands?");
    private final List<Duration> sleeps = new ArrayList<>();

    @Test
    void retriesAnOutageWithExponentialBackoff() {
        FakeModelClient fake = FakeModelClient.failingFirst(2, "Amsterdam");
        ModelClient client = new RetryingModelClient(fake, 3, Duration.ofMillis(200), sleeps::add);

        assertThat(client.complete(prompt).text()).isEqualTo("Amsterdam");
        assertThat(fake.calls()).hasSize(3);
        assertThat(sleeps).containsExactly(Duration.ofMillis(200), Duration.ofMillis(400));
    }

    @Test
    void givesUpAfterTheLastAttempt() {
        FakeModelClient fake = FakeModelClient.failingFirst(5, "Amsterdam");
        ModelClient client = new RetryingModelClient(fake, 3, Duration.ofMillis(200), sleeps::add);

        assertThatThrownBy(() -> client.complete(prompt)).isInstanceOf(ModelUnavailableException.class);
        assertThat(fake.calls()).hasSize(3);
    }

    @Test
    void doesNotRetryOtherErrors() {
        ModelClient broken = p -> {
            throw new IllegalArgumentException("bad prompt");
        };
        ModelClient client = new RetryingModelClient(broken, 3, Duration.ofMillis(200), sleeps::add);

        assertThatThrownBy(() -> client.complete(prompt)).isInstanceOf(IllegalArgumentException.class);
        assertThat(sleeps).isEmpty();
    }

    @Test
    void cachesExactRepeatsAndEvictsTheLeastRecentlyUsed() {
        FakeModelClient fake = FakeModelClient.answering(p -> "answer to " + p.user());
        CachingModelClient client = new CachingModelClient(fake, 2);

        client.complete(new Prompt("s", "a"));
        client.complete(new Prompt("s", "a"));
        client.complete(new Prompt("s", "b"));
        client.complete(new Prompt("s", "c"));
        client.complete(new Prompt("s", "a"));

        assertThat(client.hits()).isEqualTo(1);
        assertThat(fake.calls()).hasSize(4);
    }

    @Test
    void stopsCallingOnceTheBudgetIsSpent() {
        FakeModelClient fake = FakeModelClient.replying("Amsterdam");
        CostMeteringModelClient client = new CostMeteringModelClient(fake, new BigDecimal("1000"), new BigDecimal("0.02"));

        client.complete(prompt);
        client.complete(prompt);

        assertThatThrownBy(() -> client.complete(prompt)).isInstanceOf(BudgetExceededException.class);
        assertThat(fake.calls()).hasSize(2);
        assertThat(client.spentUsd()).isEqualByComparingTo("0.030");
    }

    @Test
    void decoratorOrderIsADesignDecision() {
        FakeModelClient fake = FakeModelClient.replying("Amsterdam");
        CostMeteringModelClient meter = new CostMeteringModelClient(fake, new BigDecimal("1000"), new BigDecimal("1"));
        ModelClient client = new CachingModelClient(meter, 100);

        for (int i = 0; i < 10; i++) {
            client.complete(prompt);
        }

        assertThat(fake.calls()).hasSize(1);
        assertThat(meter.spentUsd()).isEqualByComparingTo("0.015");
    }
}
