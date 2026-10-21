package com.alejandrolunatech.patterns.llm.adapter;

import com.alejandrolunatech.patterns.llm.adapter.VendorSdks.TextApi;
import com.alejandrolunatech.patterns.llm.core.Completion;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.Prompt;
import com.alejandrolunatech.patterns.llm.core.Tokens;
import java.util.Objects;

/**
 * The older API has no system role and reports no usage. The adapter folds the system text into
 * the prompt and estimates tokens, and says so in the model name, so nobody bills on a guess.
 */
public final class TextApiAdapter implements ModelClient {

    private final TextApi api;
    private final String model;
    private final int maxTokens;

    public TextApiAdapter(TextApi api, String model, int maxTokens) {
        this.api = Objects.requireNonNull(api);
        this.model = Objects.requireNonNull(model);
        this.maxTokens = maxTokens;
    }

    @Override
    public Completion complete(Prompt prompt) {
        String input = prompt.system() + "\n\n" + prompt.user();
        String text = api.generate(input, maxTokens);
        return new Completion(text, model + " (estimated usage)",
                Tokens.estimate(input), Tokens.estimate(text));
    }
}
