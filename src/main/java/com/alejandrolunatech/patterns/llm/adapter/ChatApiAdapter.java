package com.alejandrolunatech.patterns.llm.adapter;

import com.alejandrolunatech.patterns.llm.adapter.VendorSdks.ChatApi;
import com.alejandrolunatech.patterns.llm.adapter.VendorSdks.ChatMessage;
import com.alejandrolunatech.patterns.llm.adapter.VendorSdks.ChatResponse;
import com.alejandrolunatech.patterns.llm.adapter.VendorSdks.VendorOverloadedException;
import com.alejandrolunatech.patterns.llm.core.Completion;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.ModelUnavailableException;
import com.alejandrolunatech.patterns.llm.core.Prompt;
import java.util.List;
import java.util.Objects;

/**
 * Maps our prompt to the vendor's messages and the vendor's errors to ours. Vendor types never
 * leave this class.
 */
public final class ChatApiAdapter implements ModelClient {

    private final ChatApi api;
    private final String model;

    public ChatApiAdapter(ChatApi api, String model) {
        this.api = Objects.requireNonNull(api);
        this.model = Objects.requireNonNull(model);
    }

    @Override
    public Completion complete(Prompt prompt) {
        ChatResponse response;
        try {
            response = api.chat(model, List.of(
                    new ChatMessage("system", prompt.system()),
                    new ChatMessage("user", prompt.user())));
        } catch (VendorOverloadedException e) {
            throw new ModelUnavailableException(model + " overloaded");
        }
        return new Completion(response.content(), model, response.promptTokens(), response.completionTokens());
    }
}
