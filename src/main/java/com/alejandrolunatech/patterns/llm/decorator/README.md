# Decorators around the model client

**Problem.** Retries, caching and cost limits are needed around every model call, but none of them belong in the business code.

**The code.** `RetryingModelClient` retries only outages, with exponential backoff and an injected sleeper. `CachingModelClient` is an exact-match LRU cache. `CostMeteringModelClient` adds up spend and refuses calls once the budget is gone. The last test shows that putting the cache outside the meter makes repeats free.

**What modern Java changes.** Same Decorator as `gof/decorator`. Injecting the sleeper keeps retry tests instant.

**When not to use it.** Caching is wrong for prompts whose answer should change, or that contain personal data you must not keep. Make it opt-in per use case.

**Tests:** [`ModelDecoratorsTest`](/src/test/java/com/alejandrolunatech/patterns/llm/decorator/ModelDecoratorsTest.java)
