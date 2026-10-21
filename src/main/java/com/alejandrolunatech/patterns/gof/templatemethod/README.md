# Template Method, and composition instead

**Problem.** Reports share an algorithm (header, rows, footer) but differ in format.

**The code.** `ReportExporter` is the classic version: a `final` method fixes the steps and subclasses fill them in. `Exporter` does the same with a record of functions, so a new format is a value, not a subclass. The tests show both produce the same CSV.

**What modern Java changes.** Lambdas make the composition version short enough that it usually wins. Inheritance is kept for cases where the steps share a lot of state.

**When not to use it.** When the steps are trivial. Then a single method with a parameter is enough.

**Tests:** [`ExporterTest`](/src/test/java/com/alejandrolunatech/patterns/gof/templatemethod/ExporterTest.java)
