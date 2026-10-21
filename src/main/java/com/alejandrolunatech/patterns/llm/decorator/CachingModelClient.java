package com.alejandrolunatech.patterns.llm.decorator;

import com.alejandrolunatech.patterns.llm.core.Completion;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.Prompt;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An exact-match LRU cache. Safe only for prompts whose answer may be reused, so it is opt-in
 * per client, never global.
 */
public final class CachingModelClient implements ModelClient {

    private final ModelClient inner;
    private final Map<Prompt, Completion> cache;
    private int hits;

    public CachingModelClient(ModelClient inner, int maxEntries) {
        this.inner = Objects.requireNonNull(inner);
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Prompt, Completion> eldest) {
                return size() > maxEntries;
            }
        };
    }

    @Override
    public synchronized Completion complete(Prompt prompt) {
        Completion cached = cache.get(prompt);
        if (cached != null) {
            hits++;
            return cached;
        }
        Completion fresh = inner.complete(prompt);
        cache.put(prompt, fresh);
        return fresh;
    }

    public synchronized int hits() {
        return hits;
    }
}
