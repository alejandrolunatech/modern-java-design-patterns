package com.alejandrolunatech.patterns.gof.decorator;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotifierDecoratorsTest {

    private final List<String> sent = new ArrayList<>();
    private final Notifier real = (recipient, message) -> sent.add(recipient + ": " + message);
    private final MutableClock clock = new MutableClock(Instant.parse("2026-10-21T09:00:00Z"));

    @Test
    void duplicatesInsideTheWindowAreDropped() {
        Notifier notifier = new DeduplicatingNotifier(real, Duration.ofMinutes(10), clock);

        notifier.send("ana", "Build failed");
        clock.advance(Duration.ofMinutes(5));
        notifier.send("ana", "Build failed");
        notifier.send("bob", "Build failed");
        clock.advance(Duration.ofMinutes(6));
        notifier.send("ana", "Build failed");

        assertThat(sent).containsExactly("ana: Build failed", "bob: Build failed", "ana: Build failed");
    }

    @Test
    void sanitizingRemovesControlCharactersAndCutsLongMessages() {
        Notifier notifier = new SanitizingNotifier(real, 12);

        notifier.send("ana", "Deploy\u0007 done\n");
        notifier.send("ana", "A very long message indeed");

        assertThat(sent).containsExactly("ana: Deploy  done", "ana: A very long…");
    }

    @Test
    void decoratorsStackAndOrderMatters() {
        Notifier notifier = new DeduplicatingNotifier(new SanitizingNotifier(real, 50), Duration.ofMinutes(10), clock);

        notifier.send("ana", "Disk full\n");
        notifier.send("ana", "Disk full\n");

        assertThat(sent).containsExactly("ana: Disk full");
    }

    static final class MutableClock extends Clock {

        private Instant now;

        MutableClock(Instant start) {
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
