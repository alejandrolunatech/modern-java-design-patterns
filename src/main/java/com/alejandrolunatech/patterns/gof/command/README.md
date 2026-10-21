# Command

**Problem.** A text editor needs undo and redo.

**The code.** Edits are immutable records (`Insert`, `Delete`). Applying an edit returns its inverse, so `Editor` keeps two stacks of edits and undo is just applying the top inverse.

**What modern Java changes.** Sealed records plus a `switch` keep the commands as data, which also makes them easy to log, send or replay.

**When not to use it.** When there is no undo, queue or audit need. Then a direct method call is simpler.

**Tests:** [`EditorTest`](/src/test/java/com/alejandrolunatech/patterns/gof/command/EditorTest.java)
