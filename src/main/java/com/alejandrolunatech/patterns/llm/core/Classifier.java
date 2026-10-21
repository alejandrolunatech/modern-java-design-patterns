package com.alejandrolunatech.patterns.llm.core;

/** Classifies a customer message. Implemented by the model path and by the rules fallback. */
@FunctionalInterface
public interface Classifier {

    Verdict classify(String customerMessage);
}
