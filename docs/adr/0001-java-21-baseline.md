# ADR 0001: Java 21 as the baseline, tested on Java 25

**Status:** accepted, 21 Oct 2026

## Context

Java 25 is the newest long-term-support release. Java 21 is the previous one, and it is what most client codebases run today. Everything this repo needs is already final in Java 21: records, sealed interfaces, pattern matching for `switch`, and record patterns.

## Decision

Compile with `--release 21`. CI builds and tests on both Java 21 and Java 25.

## Consequences

- The code runs on what most teams have, and CI proves it also runs on the newest LTS.
- Features that became final after Java 21, such as unnamed variables (`_`) and flexible constructor bodies, are not used. Where an unnamed variable would help, a named but unused one is used instead.
