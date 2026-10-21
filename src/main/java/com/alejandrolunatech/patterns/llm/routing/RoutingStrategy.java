package com.alejandrolunatech.patterns.llm.routing;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Strategy: how to pick a model. Swapping it changes cost and quality without touching callers. */
@FunctionalInterface
public interface RoutingStrategy {

    Optional<ModelProfile> choose(Task task, List<ModelProfile> candidates);

    /** The cheapest model that is good enough and within budget. */
    static RoutingStrategy cheapestGoodEnough() {
        return (task, candidates) -> candidates.stream()
                .filter(model -> model.quality() >= task.minQuality())
                .filter(model -> model.costUsd(task.estimatedTokens()).compareTo(task.maxCostUsd()) <= 0)
                .min(Comparator.comparing((ModelProfile model) -> model.costUsd(task.estimatedTokens()))
                        .thenComparing(ModelProfile::name));
    }

    /** The best model the budget allows, as long as it meets the minimum. */
    static RoutingStrategy bestWithinBudget() {
        return (task, candidates) -> candidates.stream()
                .filter(model -> model.quality() >= task.minQuality())
                .filter(model -> model.costUsd(task.estimatedTokens()).compareTo(task.maxCostUsd()) <= 0)
                .max(Comparator.comparingInt(ModelProfile::quality)
                        .thenComparing(model -> model.costUsd(task.estimatedTokens()), Comparator.reverseOrder()));
    }
}
