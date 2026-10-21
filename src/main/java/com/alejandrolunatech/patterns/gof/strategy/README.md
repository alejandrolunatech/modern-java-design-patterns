# Strategy

**Problem.** A checkout needs a shipping price, but the rule changes per shop, per campaign and per country. Hard-coding the rule into the checkout means editing it every time.

**The code.** `ShippingCost` is a one-method interface. `Checkout` receives one and never knows which. The rules (`flat`, `perStartedKilogram`, `freeAbove`) are static factory methods returning lambdas, and `freeAbove` wraps another rule, so rules compose.

**What modern Java changes.** Before Java 8 every strategy was a class. A functional interface turns most strategies into a one-line lambda, and composition replaces a class per combination.

**When not to use it.** When there is only one rule and no sign of a second. An `if` is easier to read than an interface with one implementation.

**Tests:** [`ShippingCostTest`](/src/test/java/com/alejandrolunatech/patterns/gof/strategy/ShippingCostTest.java)
