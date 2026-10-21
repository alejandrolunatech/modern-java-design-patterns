package com.alejandrolunatech.patterns.llm.guardrails;

/** Runs on the text before it reaches the model. */
@FunctionalInterface
public interface InputGuardrail {

    String apply(String text);
}
