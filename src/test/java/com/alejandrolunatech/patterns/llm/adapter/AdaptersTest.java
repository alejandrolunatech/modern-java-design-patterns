package com.alejandrolunatech.patterns.llm.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alejandrolunatech.patterns.llm.adapter.VendorSdks.ChatMessage;
import com.alejandrolunatech.patterns.llm.adapter.VendorSdks.ChatResponse;
import com.alejandrolunatech.patterns.llm.adapter.VendorSdks.VendorOverloadedException;
import com.alejandrolunatech.patterns.llm.core.Completion;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.ModelUnavailableException;
import com.alejandrolunatech.patterns.llm.core.Prompt;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdaptersTest {

    private final Prompt prompt = new Prompt("Be brief.", "Where is my parcel?");

    @Test
    void chatAdapterMapsRolesAndUsage() {
        List<ChatMessage> seen = new ArrayList<>();
        ModelClient client = new ChatApiAdapter((model, messages) -> {
            seen.addAll(messages);
            return new ChatResponse("On its way.", 12, 4);
        }, "vendor-chat-large");

        Completion completion = client.complete(prompt);

        assertThat(seen).containsExactly(new ChatMessage("system", "Be brief."), new ChatMessage("user", "Where is my parcel?"));
        assertThat(completion).isEqualTo(new Completion("On its way.", "vendor-chat-large", 12, 4));
    }

    @Test
    void chatAdapterTranslatesVendorErrorsIntoOurs() {
        ModelClient client = new ChatApiAdapter((model, messages) -> {
            throw new VendorOverloadedException();
        }, "vendor-chat-large");

        assertThatThrownBy(() -> client.complete(prompt)).isInstanceOf(ModelUnavailableException.class);
    }

    @Test
    void textAdapterFoldsTheSystemTextInAndMarksUsageAsEstimated() {
        List<String> inputs = new ArrayList<>();
        ModelClient client = new TextApiAdapter((input, maxTokens) -> {
            inputs.add(input);
            return "On its way.";
        }, "vendor-text-v1", 256);

        Completion completion = client.complete(prompt);

        assertThat(inputs).containsExactly("Be brief.\n\nWhere is my parcel?");
        assertThat(completion.model()).isEqualTo("vendor-text-v1 (estimated usage)");
        assertThat(completion.outputTokens()).isPositive();
    }

    @Test
    void callersCanSwapVendorsBecauseTheyOnlySeeThePort() {
        List<ModelClient> vendors = List.of(
                new ChatApiAdapter((model, messages) -> new ChatResponse("ok", 1, 1), "a"),
                new TextApiAdapter((input, maxTokens) -> "ok", "b", 16));

        assertThat(vendors).allSatisfy(client -> assertThat(client.complete(prompt).text()).isEqualTo("ok"));
    }
}
