# Estatia Architectural Guard - Konsist Phase (`:core:testing-architecture`)

This module houses global structural verification tests for the Estatia project. It leverages **Konsist** to perform topological analysis across the entire multi-module graph.

## 🎯 Purpose

While KSP enforces local component contracts, `:core:testing-architecture` provides **Macro-Architecture** enforcement. It ensures that the project's high-level boundaries (module dependencies, layer purity, and naming conventions) remain intact.

### High-Fidelity Diagnostics
All tests share a unified reporting format that provides the **WHAT**, **WHY**, and **HOW** for every violation, ensuring consistency with the KSP and Lint layers.

---

## ⚖️ Enforcement Categories

### 1. Layer Purity (LAW-032 / LAW-031)
- **Problem**: Android frameworks or UI logic leaking into pure domain/model layers.
- **Enforcement**: strictly forbids `android.*` or `androidx.compose.*` imports in domain packages.

### 2. Feature Isolation (LAW-003 / LAW-004)
- **Problem**: Feature modules depending on each other, causing massive build times and circular graphs.
- **Enforcement**: prevents direct coupling between feature modules; communication must happen via core abstractions.

### 3. High-Fidelity Canary Oracle (LAW-034)
- **Problem**: Static analysis tools can silently stop working due to classpath regressions.
- **Verification**: The `Law034_LintCanaryRegressionTest` scans the `:core:canary-violations` module to verify that every registered rule still detects its intended target on the exact line.

---

## 🛡️ Technical Limitations & Confidence Policy

Konsist checks are **Topological**, not semantic. They operate on imports and name patterns.

| Scenario | Enforcement Status |
| :--- | :--- |
| Standard Import | **DETECTED** (HIGH Confidence) |
| Star Import (`.*`) | **DETECTED** (HIGH Confidence) |
| Alias / Typealias | **BYPASSABLE** (HEURISTIC) |
| Fully Qualified Name inline | **BYPASSABLE** (HEURISTIC) |

Due to these limitations, mission-critical laws that must be **NON-BYPASSABLE** are duplicated in the **KSP** or **Semantic Lint** layers.

---

## ⚙️ Usage: The Architectural Ratchet

### Running Verification
```bash
./gradlew :core:testing-architecture:test
```

### The Technical Debt Baseline
Existing structural violations are grandfathered in `ArchitecturalPolicy.TechnicalDebt`. 
- **Requirement**: If you touch a baselined file, you are encouraged to resolve the violation and remove it from the debt list.
- **Integrity**: New violations are NEVER allowed to enter the debt list.

---

## 🧪 Development
1. Update `ArchitecturalPolicy.kt` with any new layer definitions or forbidden packages.
2. Implement the test using Konsist's DSL in a new `LawXXX_...Test.kt` file.
3. Add a corresponding spec to `:core:canary-violations` to verify the test's detection logic.
