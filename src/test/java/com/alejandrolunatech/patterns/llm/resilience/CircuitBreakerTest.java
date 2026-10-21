package com.alejandrolunatech.patterns.llm.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alejandrolunatech.patterns.llm.core.FakeModelClient;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.ModelUnavailableException;
import com.alejandrolunatech.patterns.llm.core.Prompt;
import com.alejandrolunatech.patterns.llm.resilience.CircuitBreakerModelClient.CircuitOpenException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class CircuitBreakerTest {

    private final Prompt prompt = new Prompt("s", "u");
    private final TestClock clock = new TestClock(Instant.parse("2026-10-21T12:00:00Z"));
    private final CircuitBreaker breaker = new CircuitBreaker(3, Duration.ofSeconds(30), clock);

    @Test
    void opensAfterConsecutiveFailuresAndStopsCallingTheModel() {
        FakeModelClient fake = FakeModelClient.failingFirst(100, "never");
        ModelClient client = new CircuitBreakerModelClient(fake, breaker);

        for (int i = 0; i < 3; i++) {
            assertThatThrownBy(() -> client.complete(prompt)).isNotInstanceOf(CircuitOpenException.class);
        }
        assertThatThrownBy(() -> client.complete(prompt)).isInstanceOf(CircuitOpenException.class)
                .isInstanceOf(ModelUnavailableException.class);
        assertThat(fake.calls()).hasSize(3);
        assertThat(breaker.state()).isInstanceOf(CircuitBreaker.State.Open.class);
    }

    @Test
    void aTrialCallAfterTheCoolDownClosesItAgain() {
        FakeModelClient fake = FakeModelClient.failingFirst(3, "back");
        ModelClient client = new CircuitBreakerModelClient(fake, breaker);
        for (int i = 0; i < 3; i++) {
            assertThatThrownBy(() -> client.complete(prompt)).isInstanceOf(ModelUnavailableException.class);
        }

        clock.advance(Duration.ofSeconds(31));

        assertThat(client.complete(prompt).text()).isEqualTo("back");
        assertThat(breaker.state()).isEqualTo(new CircuitBreaker.State.Closed(0));
    }

    @Test
    void aFailedTrialCallOpensItForAnotherCoolDown() {
        breaker.recordFailure();
        breaker.recordFailure();
        breaker.recordFailure();
        clock.advance(Duration.ofSeconds(31));

        assertThat(breaker.allowRequest()).isTrue();
        breaker.recordFailure();

        assertThat(breaker.state()).isEqualTo(new CircuitBreaker.State.Open(clock.instant().plusSeconds(30)));
        assertThat(breaker.allowRequest()).isFalse();
    }

    @Test
    void aSuccessResetsTheFailureCount() {
        breaker.recordFailure();
        breaker.recordFailure();
        breaker.recordSuccess();
        breaker.recordFailure();

        assertThat(breaker.state()).isEqualTo(new CircuitBreaker.State.Closed(1));
    }

    static final class TestClock extends Clock {

        private Instant now;

        TestClock(Instant start) {
            this.now = start;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public Instant instant() {
            return now;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }
    }
}
