package com.alejandrolunatech.patterns.llm.adapter;

import java.util.List;

/**
 * Two made-up vendor SDKs with different shapes, standing in for real ones. Neither is changed;
 * each gets an adapter to {@code ModelClient}.
 */
public final class VendorSdks {

    private VendorSdks() {
    }

    /** A chat-style API: a list of role-tagged messages in, a response with usage out. */
    public interface ChatApi {

        ChatResponse chat(String model, List<ChatMessage> messages);
    }

    public record ChatMessage(String role, String content) {
    }

    public record ChatResponse(String content, int promptTokens, int completionTokens) {
    }

    /** An older text-completion API: one string in, one string out, no usage numbers. */
    public interface TextApi {

        String generate(String prompt, int maxTokens);
    }

    /** What the chat vendor throws when it is overloaded. */
    public static final class VendorOverloadedException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public VendorOverloadedException() {
            super("vendor overloaded");
        }
    }
}
