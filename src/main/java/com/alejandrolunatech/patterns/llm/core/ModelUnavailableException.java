package com.alejandrolunatech.patterns.llm.core;

/** The model did not answer. Distinct from a wrong answer, which is a guardrail's job. */
public class ModelUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ModelUnavailableException(String message) {
        super(message);
    }
}
