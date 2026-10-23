# Observer

**Problem.** When an order ships, several parts of the system want to know: email, analytics, the warehouse. The order code should not call each of them.

**The code.** `EventBus` keeps a list of `Consumer`s. `subscribe` returns a `Subscription` that removes the listener when closed. A listener that throws is reported to an error handler and does not stop the others; neither does an error handler that throws.

**What modern Java changes.** Listeners are lambdas instead of anonymous classes, events are records in a sealed hierarchy, and the unsubscribe handle is an `AutoCloseable`.

**When not to use it.** When the reaction must be reliable across processes or restarts. In-memory observers lose events on a crash; use a transactional outbox and a message broker instead.

**Tests:** [`EventBusTest`](/src/test/java/com/alejandrolunatech/patterns/gof/observer/EventBusTest.java)
