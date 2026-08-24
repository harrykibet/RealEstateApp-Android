# core:testing

This module provides a unified **test platform** for the Estatia application. It centralizes reusable test infrastructure, fakes, chaos injection, and deterministic synchronization utilities.

## Key Features

- **Test Fixtures**: Standardized domain entities for consistent testing (e.g., `PropertyFixtures`, `UserFixtures`).
- **Chaos Injection**: Adversarial implementations of core boundaries to test resilience:
    - `ChaosNetworkClient`: Scripted network failure sequences.
    - `NetworkChaosController`: Fine-grained control over network behaviors (Timeout, 503, Delay, etc.).
- **Deterministic Scheduling**:
    - `TestScheduler`: Named synchronization points for testing race conditions and concurrency.
    - `TestClock`: Manual control over virtual time for timeout and retry logic verification.
- **Scenario Builders**: Expressive DSL for composing complex test environments (e.g., `EstatiaTestScenario.networkOffline()`).

## Architectural Principle

> **Production modules expose replaceable boundaries; `core:testing` provides adversarial implementations of those boundaries.**

### Usage

1.  **For Unit/Integration Tests**: Use `testImplementation(testFixtures(projects.core.testing))` to access the test platform.
2.  **For Previews**: Use `debugImplementation(testFixtures(projects.core.testing))` if you need standardized fixture data in your UI previews.

## Folder Structure

```text
src/testFixtures/kotlin/
├── chaos/         # Failure domains (network, database, etc.)
├── fake/          # Deterministic fakes
├── clock/         # Time control
├── coroutine/     # Concurrency and scheduling
├── fixtures/      # Reusable data models
└── scenarios/     # Reusable test environments
```

## Dependency Graph
![Module Graph](module_graph.png)
