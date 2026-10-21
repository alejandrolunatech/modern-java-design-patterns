# Facade

**Problem.** Placing an order touches inventory, payments and shipping, in a set order, with a compensation step when payment fails. Every caller repeating that sequence will get it wrong somewhere.

**The code.** `CheckoutFacade.placeOrder` is the one entry point. It reserves stock, charges, books shipping, and releases the reservation if the payment is declined. The result is a sealed type: `Placed` or `Rejected`.

**What modern Java changes.** A sealed result type replaces exceptions for expected outcomes, so callers must handle both cases.

**When not to use it.** When the facade starts to hold business rules of its own. Then it has become a service and should be designed as one.

**Tests:** [`CheckoutFacadeTest`](/src/test/java/com/alejandrolunatech/patterns/gof/facade/CheckoutFacadeTest.java)
