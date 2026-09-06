# Estatia Architecture Source of Truth (`:core:architecture`)

This module serves as the **Single Source of Truth (SSoT)** for all architectural laws, standards, and constants enforced across the Estatia codebase.

## 🎯 Purpose

In a multi-layered enforcement model (Lint, KSP, Konsist), documentation and code naturally drift apart. `:core:architecture` solves this by providing a unified registry that all enforcement tools must reference.

1.  **Unified Identity**: Every architectural rule has a unique `Law` ID (e.g., `LAW-001`).
2.  **Shared Constants**: Centralizes thresholds, allowed packages, and naming conventions used by both Lint and Konsist.
3.  **Cross-Layer Parity**: Ensures that a violation reported by a KSP compiler error uses the exact same description and ID as a Lint warning in the IDE.

## 🏗️ Design: Pure Kotlin

This is a **Pure Kotlin** (Non-Android) module. This is a critical design choice to ensure that:
- **Lint** (which runs in the IDE/CLI) can depend on it without needing the Android Gradle Plugin.
- **KSP** (which runs during compilation) can access it with minimal overhead.
- **Konsist** (unit tests) can verify project structure against these constants.

## ⚖️ The Law Registry

The core of this module is the [`Law`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/architecture/src/main/kotlin/com/estatia/realestate/apps/core/architecture/Law.kt) enum. 

### Usage in Detectors

```kotlin
import com.estatia.realestate.apps.core.architecture.Law

val ISSUE = EstatiaIssue.create(
    id = "MyViolation",
    architectureLaw = Law.LAW_008, // Mandatory enum reference
    ...
)
```

### Usage in KSP Processors

```kotlin
logger.error("Architecture Violation (${Law.LAW_009.id}): ${Law.LAW_009.description}")
```

## 🛡️ Documentation Parity

By using this module, we enable automated "Doc Parity" tests. Our CI suite parses the `lint/README.md` and cross-checks it against the `Law` enum in this module. If a law is added to the code but not the documentation (or vice versa), the build fails.

---

## 🛠️ Adding a New Law

1.  Define the new entry in the `Law` enum in this module.
2.  Add a corresponding row to the table in `lint/README.md`.
3.  Implement the enforcement logic in the appropriate layer (Lint for semantics, KSP for contracts, Konsist for structure).
4.  The `DocParityTest` will automatically ensure you haven't missed any documentation steps.
