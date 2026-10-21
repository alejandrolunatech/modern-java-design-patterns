package com.alejandrolunatech.patterns.llm.routing;

import java.util.List;
import java.util.Objects;

public final class ModelRouter {

    private final List<ModelProfile> models;
    private final RoutingStrategy strategy;

    public ModelRouter(List<ModelProfile> models, RoutingStrategy strategy) {
        this.models = List.copyOf(models);
        this.strategy = Objects.requireNonNull(strategy);
    }

    /** Fails loudly when nothing fits, instead of quietly calling an expensive model. */
    public ModelProfile route(Task task) {
        return strategy.choose(task, models).orElseThrow(() -> new NoModelFitsException(task));
    }

    public static final class NoModelFitsException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        NoModelFitsException(Task task) {
            super("no model meets quality " + task.minQuality() + " within USD " + task.maxCostUsd().toPlainString());
        }
    }
}
