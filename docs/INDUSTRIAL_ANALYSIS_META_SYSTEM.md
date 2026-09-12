# Estatia — Industrial Analysis Meta-System

## Overview

The **Industrial Analysis Meta-System** is Estatia's automated governance engine. Unlike standard static analysis which relies on naming conventions and "best-effort" heuristics, Estatia uses a **Semantic-First** approach. This system ensures that 100% of the functional codebase is governed by architectural laws, leaving no room for "Shadow Layers" or accidental bypasses.

---

## 🛡️ The Unified Rule Contract

Estatia operates on a **Single-Source-of-Truth** model for governance. All architectural laws are defined in the central **[`Law`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/architecture/src/main/kotlin/com/estatia/realestate/apps/core/architecture/Law.kt)** registry.

### Metadata Inheritance
To eliminate configuration drift, every diagnostic in the system (Lint, KSP, README) is **derived** from this contract:
- **Rationales & Recommendations**: Defined once in `Law.kt`, automatically reflected in Android Studio IDE help text.
- **Ownership**: Every law is assigned to a specific team (e.g., `@estatia/architects`), ensuring clear accountability.
- **Automated Severity**: Severity is derived from the law's `Enforcement` tier (BLOCK $\rightarrow$ FATAL, WARN $\rightarrow$ WARNING).

---

## 🎯 The Core Constraint: Mandatory Architectural Identity (LAW-041)

The foundation of this system is the **Identity Mandate**. Every top-level functional component in governed modules **must** explicitly declare its architectural role via a recognized annotation.

### Identity vs. Truth: Verification Invariants
Claiming a role is mandatory, but that claim must satisfy **Structural Invariants**:
- **Path Verification**: A `@Repository` must live in `:core:data`.
- **Inheritance Verification**: A `@ViewModelMarker` must inherit from `androidx.lifecycle.ViewModel`.
- **Contract Verification**: A `@Contract` must be an `interface`.
- **Model Verification**: A `@DomainModel` must be a `data`, `sealed`, or `value class`.

---

## 🚦 Governance Quality Oracle

Estatia does not just "have rules"; it **measures their trustworthiness**. The **[`GovernanceQualityOracle`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/lint/src/test/kotlin/com/estatia/realestate/apps/lint/policy/GovernanceQualityOracle.kt)** provides mathematical proof of our enforcement reliability.

### The Quality Floor
We enforce strict Service Level Indicators (SLIs) for our architectural detectors:
- **Recall (Industrial Safety)**: Must be **100%** for all `BLOCK` level laws. If a detector misses a single violation in our authoritative specimens, the build fails.
- **Precision (Developer Trust)**: Must be **100%** for "Certain" laws. This ensures zero diagnostic "noise" and prevents "suppression rot."

---

## 🕵️ Tiered Adversarial Benchmarking

To achieve industrial-grade robustness, every law is stress-tested against an **Adversarial Hub** organized into three complexity tiers:

| Tier | Name | Target | Bypass Techniques |
| :--- | :--- | :--- | :--- |
| **Tier 1** | **Syntactic** | Pattern Matching | Slightly non-standard naming or formatting. |
| **Tier 2** | **Semantic** | Symbol Resolution | Type Aliases, Property Delegates, Extension Functions, Nested Generics. |
| **Tier 3** | **Dynamic** | Analysis Limits | Reflection (`getDeclaredMethod`), Dynamic Proxies, Type Erasure (`Any` casts). |

This benchmarking ensures that architectural boundaries cannot be bypassed via standard language trickery.

---

## 🏗️ Hardened Semantic Resolution

Our enforcement engines are hardened to provide high-fidelity diagnostics:

- **Reflection Guard**: Aggressively blocks usage of `javaClass.getDeclaredMethod`, `invoke`, or `Proxy` in governed layers when targeting architectural components.
- **Dangerous Local Tracking**: Tracks the *origin* of local variables. If a shared field is extracted into a local `val` and then mutated, the system correctly identifies it as a thread-safety violation.
- **Recursive Generic Audit**: Scans deeply nested generic arguments (e.g., `Map<String, Set<ArrayList<Int>>>`) to ensure implementation types never leak through boundaries.
- **Delegate Awareness**: Automatically resolves property delegates (`by lazy`, etc.) to audit the underlying state container.

---

## The Semantic Taxonomy

| Role | Target | Key Law Enabled |
| :--- | :--- | :--- |
| **`@Contract`** | Interface | Enforces safety rules (Result wrapping) at the behavioral boundary. |
| **`@Repository`** | Class | Enforces data source isolation and mandatory error handling. |
| **`@UseCase`** | Class | Enforces constructor purity and domain-only dependencies. |
| **`@DataSource`** | Class | Identifies IO-heavy components for mandatory thread-confinement. |
| **`@DomainModel`** | Data Class | Enforces **LAW-032** (Pure Kotlin only, no Frameworks). |
| **`@Manager`** | Class | Enforces strict synchronization on state-holding singletons. |
| **`@UiScreen`** | Function | Marks a top-level Screen Composable (Target for Navigation). |

---

> [!IMPORTANT]
> **Identity is Intent.**
> In Estatia, we do not guess the purpose of code. We mandate that the developer declares it, and we mathematically verify that the declaration is true and the implementation is safe.
