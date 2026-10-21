package com.alejandrolunatech.patterns.llm.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ModelRouterTest {

    private final ModelProfile small = new ModelProfile("small", 5, new BigDecimal("0.20"));
    private final ModelProfile medium = new ModelProfile("medium", 7, new BigDecimal("1.00"));
    private final ModelProfile large = new ModelProfile("large", 9, new BigDecimal("15.00"));
    private final List<ModelProfile> models = List.of(large, small, medium);

    @Test
    void cheapestGoodEnoughPicksTheCheapestModelThatMeetsTheBar() {
        ModelRouter router = new ModelRouter(models, RoutingStrategy.cheapestGoodEnough());

        assertThat(router.route(new Task(6, 2_000, new BigDecimal("0.01")))).isEqualTo(medium);
        assertThat(router.route(new Task(1, 2_000, new BigDecimal("0.01")))).isEqualTo(small);
    }

    @Test
    void bestWithinBudgetSpendsTheBudgetOnQuality() {
        ModelRouter router = new ModelRouter(models, RoutingStrategy.bestWithinBudget());

        assertThat(router.route(new Task(1, 2_000, new BigDecimal("0.01")))).isEqualTo(medium);
        assertThat(router.route(new Task(1, 2_000, new BigDecimal("0.05")))).isEqualTo(large);
    }

    @Test
    void failsInsteadOfQuietlyOverspending() {
        ModelRouter router = new ModelRouter(models, RoutingStrategy.cheapestGoodEnough());

        assertThatThrownBy(() -> router.route(new Task(9, 2_000, new BigDecimal("0.01"))))
                .isInstanceOf(ModelRouter.NoModelFitsException.class)
                .hasMessageContaining("quality 9");
    }

    @Test
    void costIsExactDecimalArithmetic() {
        assertThat(large.costUsd(2_000)).isEqualByComparingTo("0.03");
    }
}
