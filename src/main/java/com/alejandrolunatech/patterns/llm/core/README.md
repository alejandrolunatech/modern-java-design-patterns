# The model port (Ports and Adapters)

**Problem.** Code that calls an LLM vendor SDK directly is hard to test, locked to one vendor, and has nowhere to put retries, budgets or guardrails.

**The code.** `ModelClient` is the one interface the application uses. `Prompt` and `Completion` are records. `FakeModelClient` is a deterministic stand-in used by every test, so the build needs no key, no network and no money. `Classifier` and `Verdict` describe the example task: triaging customer messages.

**What modern Java changes.** Records as value types and a sealed `Verdict` with only two outcomes, `Accepted` or `NeedsReview`, so nothing half-decided slips through.

**When not to use it.** Never skip the port in production code. Skip the fake only if you have recorded real responses to replay instead.

**Tests:** [`SupportTriageExampleTest`](/src/test/java/com/alejandrolunatech/patterns/llm/SupportTriageExampleTest.java)
