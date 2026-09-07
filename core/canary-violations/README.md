# Estatia Architectural Canary (`:core:canary-violations`)

This module is a **deliberate failure zone**. It contains intentional violations of nearly every architectural law enforced in the Estatia codebase.

## 🎯 Purpose

While synthetic unit tests in the `:lint` and `:core:ksp-architecture` modules verify that detectors work in isolation, they cannot guarantee:
1.  **Crash Resilience**: That a detector won't throw an exception (e.g., `NullPointerException` on `resolve()`) when running against a real multi-module classpath.
2.  **Resolution Fidelity**: That a detector correctly identifies a violation when types are resolved through multiple module boundaries (e.g., a Feature ViewModel referencing a Core Repository).
3.  **Heartbeat Verification**: That the custom Lint JAR is actually being loaded and executed by the Gradle build system.

## ⚖️ Coverage Families

> [!IMPORTANT]
> The Canary does not yet cover all 35+ laws. It is focused on **High-Risk Detector Families** that are most prone to regression during classpath changes or AGP updates.

### Validated Families:
1.  **Architecture Layering**: Feature Coupling, Infrastructure Leakage.
2.  **API Contracts**: Visibility Modifiers, Interface Enforcement, Result Wrapping.
3.  **Concurrency Safety**: Hardcoded Dispatchers, Secret Concurrency.
4.  **UI Governance**: Compose Architecture Leakage, Business Logic in Views.
5.  **Security/Privacy**: PII Logging, Sensitive Data Handling.
6.  **Resource Management**: Lifecycle Leaks.

## 🛡️ CI Integration

The `:core:testing-architecture` module contains a regression test, [`Law034_LintCanaryRegressionTest`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/testing-architecture/src/test/kotlin/com/estatia/realestate/apps/core/testing_architecture/Law034_LintCanaryRegressionTest.kt), which:
1.  Parses the Lint report generated from this module.
2.  Asserts that every expected violation ID was actually found.
3.  Fails the build if any detector "silently passed" due to a regression in resolution logic.

## ⚠️ Warning

**DO NOT FIX THE VIOLATIONS IN THIS MODULE.** 

Fixing them will break the regression tests. If you add a new architectural law, you **MUST** add a corresponding violation here and update the regression test.
