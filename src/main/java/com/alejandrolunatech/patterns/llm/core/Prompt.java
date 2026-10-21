package com.alejandrolunatech.patterns.llm.core;

import java.util.Objects;

/** What we send to a model. A record, so it can be a cache key as it is. */
public record Prompt(String system, String user) {

    public Prompt {
        Objects.requireNonNull(system, "system");
        Objects.requireNonNull(user, "user");
    }
}
