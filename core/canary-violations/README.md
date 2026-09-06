# Estatia Architectural Canary (`:core:canary-violations`)

This module is a **deliberate failure zone**. It contains intentional violations of nearly every architectural law enforced in the Estatia codebase.

## 🎯 Purpose

While synthetic unit tests in the `:lint` and `:core:ksp-architecture` modules verify that detectors work in isolation, they cannot guarantee:
1.  **Crash Resilience**: That a detector won't throw an exception (e.g., `NullPointerException` on `resolve()`) when running against a real multi-module classpath.
2.  **Resolution Fidelity**: That a detector correctly identifies a violation when types are resolved through multiple module boundaries (e.g., a Feature ViewModel referencing a Core Repository).
3.  **Heartbeat Verification**: That the custom Lint JAR is actually being loaded and executed by the Gradle build system.

## ⚖️ How it works

The module contains a single file, [`CanaryViolations.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/canary-violations/src/main/kotlin/com/estatia/realestate/apps/core/canary_violations/CanaryViolations.kt), which is wired to trigger the following laws:

-   **LAW-001**: Business Logic in Compose (launch/collect in Composable).
-   **LAW-003**: Infrastructure Leakage (Importing Database/Network implementations in high layers).
-   **LAW-004**: Feature Coupling (Cross-module feature imports).
-   **LAW-006**: Hardcoded Dispatchers (Direct `Dispatchers.IO` usage).
-   **LAW-008**: Missing Visibility Modifiers & Interface Contracts.
-   **LAW-009**: Missing Result Wrappers in Repositories.
-   **LAW-010**: Sensitive Logging (PII leakage).
-   **LAW-016**: Mutable State Ownership.
-   **LAW-018**: ViewModel Single Source of Truth (Multiple StateFlows).
-   **LAW-019**: Secret Concurrency (Launching on external scope in suspend functions).
-   **LAW-023**: Lifecycle Leaks (Storing Activity/Context in long-lived components).
-   **LAW-025**: Mutable Singleton Read in Compose.
-   **LAW-027**: Compose Architecture Leakage (Direct Repository calls).

## 🛡️ CI Integration

The `:core:testing-architecture` module contains a regression test, [`Law034_LintCanaryRegressionTest`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/testing-architecture/src/test/kotlin/com/estatia/realestate/apps/core/testing_architecture/Law034_LintCanaryRegressionTest.kt), which:
1.  Parses the Lint report generated from this module.
2.  Asserts that every expected violation ID was actually found.
3.  Fails the build if any detector "silently passed" due to a regression in resolution logic.

## ⚠️ Warning

**DO NOT FIX THE VIOLATIONS IN THIS MODULE.** 

Fixing them will break the regression tests. If you add a new architectural law, you **MUST** add a corresponding violation here and update the regression test.
