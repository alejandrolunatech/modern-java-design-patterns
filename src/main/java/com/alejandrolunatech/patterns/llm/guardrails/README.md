# Chain of Responsibility for guardrails

**Problem.** A model's answer is a proposal. It can be malformed, use a label that doesn't exist, or be unsure. Personal data should not reach the vendor at all.

**The code.** `GuardedClassifier` runs input guardrails (`PiiMasker`) before the call, wraps the customer's text in tags as data, parses a strict two-line output contract, and runs `OutputCheck`s in order. The first problem sends the message to a person.

**What modern Java changes.** The checks are functional interfaces and the result is a sealed `Verdict`, so the caller can't forget the review path.

**When not to use it.** Regex masking is a baseline, not a guarantee. In production add a dedicated PII detector and log what was masked, never the original.

**Tests:** [`GuardedClassifierTest`](/src/test/java/com/alejandrolunatech/patterns/llm/guardrails/GuardedClassifierTest.java)
