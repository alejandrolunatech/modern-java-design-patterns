# Chain of Responsibility

**Problem.** Expense approval runs through several rules: forbidden categories, automatic approval for small amounts, then a manager. The order is the policy.

**The code.** Each `Approver` returns a decision or `Optional.empty()` to pass. `ApprovalChain` walks the list and stops at the first decision; nobody deciding means escalation. No handler holds a `next` pointer.

**What modern Java changes.** Functional interfaces and `Optional` remove the linked-list plumbing of the textbook version. The chain is just a `List`.

**When not to use it.** When every handler must run (that is a pipeline), or when the rules depend on each other. Then write the rules out explicitly.

**Tests:** [`ApprovalChainTest`](/src/test/java/com/alejandrolunatech/patterns/gof/chain/ApprovalChainTest.java)
