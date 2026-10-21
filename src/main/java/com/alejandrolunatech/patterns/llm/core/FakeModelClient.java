package com.alejandrolunatech.patterns.llm.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A deterministic stand-in for a real model. Every test and example in this repo uses it, so the
 * build needs no network, no API key and no money, and a test means the same thing every run.
 */
public final class FakeModelClient implements ModelClient {

    private final Function<Prompt, String> answer;
    private final List<Prompt> calls = new ArrayList<>();
    private int failuresLeft;

    private FakeModelClient(Function<Prompt, String> answer, int failuresFirst) {
        this.answer = Objects.requireNonNull(answer);
        this.failuresLeft = failuresFirst;
    }

    public static FakeModelClient replying(String text) {
        return new FakeModelClient(prompt -> text, 0);
    }

    public static FakeModelClient answering(Function<Prompt, String> answer) {
        return new FakeModelClient(answer, 0);
    }

    /** Throws {@link ModelUnavailableException} for the first {@code times} calls, then answers. */
    public static FakeModelClient failingFirst(int times, String text) {
        return new FakeModelClient(prompt -> text, times);
    }

    @Override
    public synchronized Completion complete(Prompt prompt) {
        calls.add(prompt);
        if (failuresLeft > 0) {
            failuresLeft--;
            throw new ModelUnavailableException("fake outage");
        }
        String text = answer.apply(prompt);
        return new Completion(text, "fake-model", Tokens.estimate(prompt.system() + prompt.user()), Tokens.estimate(text));
    }

    public synchronized List<Prompt> calls() {
        return List.copyOf(calls);
    }
}
