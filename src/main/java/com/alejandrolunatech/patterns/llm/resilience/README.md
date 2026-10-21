# Circuit Breaker and fallback

**Problem.** When the vendor is down, every call waits for a timeout, and the queue backs up. The business still needs an answer.

**The code.** `CircuitBreaker` is a small sealed state machine: `Closed`, `Open` until a time, and `HalfOpen` for one trial call. `CircuitBreakerModelClient` uses it around the model. `FallbackClassifier` switches to the `KeywordClassifier` rules when the model path is unavailable.

**What modern Java changes.** The breaker's states are records in a sealed interface, and the transition uses a `switch` with a guard. A `Clock` is injected so the tests control the cool-down.

**When not to use it.** When a library such as Resilience4j is already in the project. Use it there; this version exists to show the mechanism.

**Tests:** [`CircuitBreakerTest`](/src/test/java/com/alejandrolunatech/patterns/llm/resilience/CircuitBreakerTest.java) and [`FallbackClassifierTest`](/src/test/java/com/alejandrolunatech/patterns/llm/resilience/FallbackClassifierTest.java)
