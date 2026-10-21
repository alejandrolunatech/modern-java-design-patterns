# ADR 0002: No runtime dependencies

**Status:** accepted, 21 Oct 2026

## Context

This repo exists to show patterns. A framework or library hides the mechanism the reader came to see, and each dependency is something to update and audit.

## Decision

The main code uses only the JDK. Test code uses JUnit, AssertJ and ArchUnit.

## Consequences

- The circuit breaker, retry and cache are hand-written. In a real Spring service, Resilience4j and Spring's cache abstraction would replace them; the notes say so.
- The model's output contract is two plain `key=value` lines instead of JSON, so no JSON library is needed. The companion Spring Boot repo uses JSON with a schema for the same job.
