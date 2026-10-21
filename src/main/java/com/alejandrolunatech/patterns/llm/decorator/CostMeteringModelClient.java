package com.alejandrolunatech.patterns.llm.decorator;

import com.alejandrolunatech.patterns.llm.core.Completion;
import com.alejandrolunatech.patterns.llm.core.ModelClient;
import com.alejandrolunatech.patterns.llm.core.Prompt;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Counts what every call costs and refuses new calls once the budget is spent. The check runs
 * before the call, so one call can overshoot by at most its own cost.
 */
public final class CostMeteringModelClient implements ModelClient {

    private static final BigDecimal MILLION = BigDecimal.valueOf(1_000_000);

    private final ModelClient inner;
    private final BigDecimal usdPerMillionTokens;
    private final BigDecimal budgetUsd;
    private BigDecimal spentUsd = BigDecimal.ZERO;

    public CostMeteringModelClient(ModelClient inner, BigDecimal usdPerMillionTokens, BigDecimal budgetUsd) {
        this.inner = Objects.requireNonNull(inner);
        this.usdPerMillionTokens = Objects.requireNonNull(usdPerMillionTokens);
        this.budgetUsd = Objects.requireNonNull(budgetUsd);
    }

    @Override
    public synchronized Completion complete(Prompt prompt) {
        if (spentUsd.compareTo(budgetUsd) >= 0) {
            throw new BudgetExceededException(spentUsd, budgetUsd);
        }
        Completion completion = inner.complete(prompt);
        int tokens = completion.inputTokens() + completion.outputTokens();
        spentUsd = spentUsd.add(usdPerMillionTokens.multiply(BigDecimal.valueOf(tokens)).divide(MILLION));
        return completion;
    }

    public synchronized BigDecimal spentUsd() {
        return spentUsd;
    }

    public static final class BudgetExceededException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        BudgetExceededException(BigDecimal spent, BigDecimal budget) {
            super("budget spent: USD " + spent.toPlainString() + " of " + budget.toPlainString());
        }
    }
}
