# Strategy for model routing

**Problem.** Different calls need different trade-offs: a cheap model for simple tasks, a strong one when quality matters, and never more than the budget.

**The code.** `RoutingStrategy` picks a `ModelProfile` for a `Task` (minimum quality, expected tokens, maximum cost). Two strategies: `cheapestGoodEnough` and `bestWithinBudget`. When nothing fits, the router throws instead of quietly calling an expensive model. Money is `BigDecimal`.

**What modern Java changes.** The same functional-interface Strategy as `gof/strategy`, applied to cost and quality.

**When not to use it.** When you only use one model. Add routing when you have eval data that shows a cheaper model is good enough for part of the traffic.

**Tests:** [`ModelRouterTest`](/src/test/java/com/alejandrolunatech/patterns/llm/routing/ModelRouterTest.java)
