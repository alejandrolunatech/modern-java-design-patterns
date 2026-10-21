# Working in this repo

A portfolio repo: classic design patterns in modern Java, and patterns for LLM-backed systems. Read `README.md` first.

- Code is written fresh for this repo. Never copy code, names or domain details from an employer, a client, or any paid task platform.
- Every pattern has three parts: its code in its own package, a test class, and a `README.md` note (problem, the code, what modern Java changes, when not to use it). A change to one updates the others.
- Patterns stay independent: GoF packages don't depend on each other, LLM packages share only `llm.core`. `ArchitectureTest` enforces this.
- The main code has no runtime dependencies (ADR 0002). Tests never call a network or a real model (ADR 0003).
- Compile target is Java 21 (ADR 0001); CI also runs Java 25. The build treats warnings as errors.
- Run `./mvnw verify` before every push.
- Claude works on a feature branch and opens a pull request. Never push to `main`, never merge.
- `docs/LEARNING-NOTES.md` and the "Done so far" table in `KATAS.md` are the owner's own writing. Don't fill them in.
