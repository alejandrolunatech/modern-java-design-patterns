# Factory with sealed types

**Problem.** Payment details arrive as text from a form or an API. The rest of the code should work with typed values and never with raw strings or constructors.

**The code.** `PaymentMethod.parse` is a static factory that returns one of three records. The interface is sealed, so `Fees.feeCents` can switch over the types with no `default` branch.

**What modern Java changes.** Sealed interfaces (Java 17) and pattern matching for `switch` (Java 21) make the set of types closed and checked by the compiler. Add a fourth payment method and `Fees` stops compiling until someone decides its fee. The card record keeps only the last four digits, so the full number never enters the domain.

**When not to use it.** When the set of types must stay open to other modules or plugins. Then a regular interface plus a registry fits better than a sealed one.

**Tests:** [`PaymentMethodTest`](/src/test/java/com/alejandrolunatech/patterns/gof/factory/PaymentMethodTest.java)
