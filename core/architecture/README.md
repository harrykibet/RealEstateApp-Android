# Estatia Architecture Source of Truth (`:core:architecture`)

This module serves as the **Single Source of Truth (SSoT)** for all architectural laws, standards, and constants enforced across the Estatia codebase.

## 🎯 Purpose

In a multi-layered enforcement model (Lint, KSP, Konsist), documentation and code naturally drift apart. `:core:architecture` solves this by providing a unified registry that all enforcement tools must reference.

1.  **Unified Identity**: Every architectural rule has a unique `Law` ID (e.g., `LAW-001`).
2.  **Multi-Dimensional Risk Model**: Every law is categorized by **Risk**, **Confidence**, and **Enforcement** level.
3.  **Actionable Intelligence**: Centralizes the `rationale` and `recommendation` for every rule, enabling high-fidelity diagnostics.

## 🏗️ Design: Pure Kotlin

This is a **Pure Kotlin** (Non-Android) module. This ensures that:
- **Lint** (IDE/CLI) can depend on it without needing the Android Gradle Plugin.
- **KSP** (Compilation) can access it with minimal overhead.
- **Konsist** (Verification) can verify project topology against these constants.

---

## 🚦 The 3D Enforcement Model

We evaluate every architectural rule along three distinct axes:

### 1. Risk (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`)
Describes the potential impact on production. `CRITICAL` risk rules involve security breaches, immediate crashes, or non-deterministic data races.

### 2. Confidence (`CERTAIN`, `HIGH`, `HEURISTIC`)
Describes the technical reliability of the detector. 
- `CERTAIN`: Symbol-resolved analysis (KSP/Lint). No bypass possible.
- `HIGH`: Topological analysis (Konsist). Highly reliable for boundaries.
- `HEURISTIC`: Pattern matching (Regex/Grep). Advisory feedback.

### 3. Enforcement (`BLOCK`, `WARN`, `INFO`)
The automated action taken. `BLOCK` rules will fail the build immediately.

---

## ⚖️ The Law Registry

The core of this module is the [`Law`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/architecture/src/main/kotlin/com/estatia/realestate/apps/core/architecture/Law.kt) enum. 

### Usage in Actionable Diagnostics

```kotlin
// In KSP or Lint
val fullMessage = """
    |WHAT: $message
    |WHY: ${law.rationale}
    |RECOMMENDED: ${law.recommendation}
    |[LAW: ${law.id} | RISK: ${law.risk} | CONFIDENCE: ${law.confidence}]
""".trimMargin()
```

## 🛡️ Governance Sync

CI tasks automatically enforce parity between this module and the documentation in `lint/README.md`. If a law's metadata (Risk, Confidence, Enforcement) changes in code but not in docs, the build fails.

---

## 🛠️ Adding a New Law

1.  Define the new entry in the `Law` enum with its 3D metadata and actionable guidance.
2.  Add a corresponding row to the table in `lint/README.md`.
3.  Implement a **Modular Specification** in `:core:canary-violations` (Positive and Negative examples).
4.  Implement the enforcement logic in the appropriate layer (Lint, KSP, or Konsist).
