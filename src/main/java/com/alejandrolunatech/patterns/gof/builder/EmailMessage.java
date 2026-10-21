package com.alejandrolunatech.patterns.gof.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable value with a few required and several optional parts. The record holds the data
 * and validates it once; the builder only exists to make the optional parts readable.
 */
public record EmailMessage(String from, List<String> to, List<String> cc, Optional<String> replyTo,
                           String subject, String body) {

    public EmailMessage {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(subject, "subject");
        Objects.requireNonNull(body, "body");
        to = List.copyOf(to);
        cc = List.copyOf(cc);
        if (to.isEmpty()) {
            throw new IllegalArgumentException("at least one recipient is required");
        }
        if (subject.isBlank()) {
            throw new IllegalArgumentException("subject must not be blank");
        }
    }

    public static Builder from(String sender) {
        return new Builder(sender);
    }

    public static final class Builder {

        private final String from;
        private final List<String> to = new ArrayList<>();
        private final List<String> cc = new ArrayList<>();
        private String replyTo;
        private String subject = "";
        private String body = "";

        private Builder(String from) {
            this.from = from;
        }

        public Builder to(String recipient) {
            to.add(recipient);
            return this;
        }

        public Builder cc(String recipient) {
            cc.add(recipient);
            return this;
        }

        public Builder replyTo(String address) {
            this.replyTo = address;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        /** All validation lives in the record's constructor, so a built message is always valid. */
        public EmailMessage build() {
            return new EmailMessage(from, to, cc, Optional.ofNullable(replyTo), subject, body);
        }
    }
}
