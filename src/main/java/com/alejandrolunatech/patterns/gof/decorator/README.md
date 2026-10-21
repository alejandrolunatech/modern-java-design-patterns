# Decorator

**Problem.** Notifications need extra behaviour, such as dropping duplicates and cleaning text, without changing the code that sends them.

**The code.** Each decorator implements `Notifier` and wraps another `Notifier`. `DeduplicatingNotifier` takes a `Clock`, so the tests move time forward instead of sleeping. The last test shows that the order of wrapping changes the result.

**What modern Java changes.** The idea is unchanged. What changed is testability: injecting `java.time.Clock` is now the normal way to make time-based code deterministic.

**When not to use it.** When the behaviour is cross-cutting across many interfaces (logging, transactions). A framework aspect or a proxy is less code there.

**Tests:** [`NotifierDecoratorsTest`](/src/test/java/com/alejandrolunatech/patterns/gof/decorator/NotifierDecoratorsTest.java)
