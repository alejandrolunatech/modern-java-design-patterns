# State, as a sealed state machine

**Problem.** An order moves through draft, placed, paid, shipped and cancelled, and some moves are illegal, such as cancelling after payment.

**The code.** Each state is a record that carries only the data valid in that state. `OrderStateMachine.next` is one pure function from (state, command) to the next state. Every legal move is listed, and everything else throws.

**What modern Java changes.** The classic State pattern puts behaviour in one class per state. Sealed records with nested pattern-matching switches put the whole lifecycle on one screen, which is easier to review and test.

**When not to use it.** When each state has a lot of distinct behaviour beyond transitions. Then the classic pattern, one class per state, can still read better.

**Tests:** [`OrderStateMachineTest`](/src/test/java/com/alejandrolunatech/patterns/gof/state/OrderStateMachineTest.java)
