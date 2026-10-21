# ADR 0003: A fake model client, and no network in tests

**Status:** accepted, 21 Oct 2026

## Context

Tests that call a real model are slow, cost money, need a secret, and can give a different answer on each run. A test that sometimes fails teaches people to ignore failures.

## Decision

Every test uses `FakeModelClient`, which returns scripted answers and can simulate outages. No code in this repo calls a real model.

## Consequences

- `./mvnw verify` works offline, with no API key, and gives the same result every time.
- The tests prove how the code handles answers, not how good a real model's answers are. Measuring that needs evals with labelled data against a real model; they live in the companion Spring Boot repo, on a separate, manually started job with a spending limit.
