# Visitor, replaced by pattern matching

**Problem.** An expression tree needs several operations: evaluate, print, simplify. Adding an operation should not mean editing every node class.

**The code.** `Expr` is a sealed interface of records. Each operation in `Operations` is a method with an exhaustive `switch` using record patterns. `simplify` shows nested patterns with guards. The `classic` package keeps the textbook Visitor for comparison.

**What modern Java changes.** Record patterns (Java 21) give the double dispatch Visitor was invented for, without `accept` methods. The compiler still checks that every node type is handled.

**When not to use it.** When the node types live in a library you can't seal. Then the classic Visitor is still the tool.

**Tests:** [`OperationsTest`](/src/test/java/com/alejandrolunatech/patterns/gof/visitor/OperationsTest.java)
