# Estatia Architectural Guard - Konsist Phase (`:core:testing-architecture`)

This module houses the **Architectural Source of Truth** and global structural verification tests for the Estatia project. It leverages **Konsist** to perform deep static analysis across the entire project graph.

## 🎯 Purpose

While Android Lint focuses on local file-level patterns and KSP enforces high-precision semantic rules during compilation, `:core:testing-architecture` provides **Level 2 (Global Structural)** enforcement. It ensures that the project's macro-architecture (module boundaries, layer purity, and naming conventions) remains intact as the codebase grows.

### The Hybrid Enforcement Model
- **Level 1 (Lint)**: Fast, IDE-integrated feedback for common coding mistakes.
- **Level 2 (Konsist)**: Global structural tests that view the project as a single graph.
- **Level 3 (KSP)**: 100% precise compiler errors for mission-critical architectural boundaries.

---

## ⚠️ Known Limitations (Syntactic vs. Semantic Analysis)

It is important to understand that **Konsist checks in this module are syntactic**, meaning they operate on code structure and string matching, not full semantic resolution.

### Implications:
1.  **Resolution Blindness**: Konsist may not catch violations hidden behind `typealias`, star imports (`import .*`), or fully qualified names used inline without an import statement.
2.  **String Matching**: Some rules use substring matching (e.g., `contains("HashMap")`). This can lead to **false positives** if a class name contains a forbidden string but is legitimately architected (e.g., `UserHashMapAdapter`).
3.  **Severity Policy**: Due to these limitations, **Konsist rules should generally not be used for FATAL enforcement** if a high degree of precision is required. Mission-critical, non-bypassable laws are instead enforced via **KSP** or **Android Lint** with full UAST resolution.

---

## ⚖️ Enforced Laws

The following laws (as defined in the central [`Law`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/architecture/src/main/kotlin/com/estatia/realestate/apps/core/architecture/Law.kt) enum) are primary targets for Konsist enforcement:

### Layer Purity & Isolation
- **LAW-032 (Pure Domain/Model)**: Enforces that `:core:domain` and `:core:model` remain pure Kotlin/Java, strictly forbidding dependencies on Android Frameworks or infrastructure libraries.
- **LAW-003 (Feature Isolation)**: Prevents feature modules from depending on other feature modules (except for shared utilities) or direct infrastructure implementations.
- **LAW-031 (Layer Mixing)**: Ensures that business logic components and ViewModels do not reference UI frameworks like Compose or Android Views.

### API & State Integrity
- **LAW-008 / LAW-002 (Public API Purity)**: Scans all public properties and functions to ensure they do not expose mutable containers or implementation-specific types.
- **LAW-004 (Naming Consistency)**: Validates that package names strictly follow the module structure.

### CI Governance
- **LAW-034 (Canary Regression)**: Runs against a deliberate "violation module" to ensure the enforcement system hasn't regressed.
- **LAW-035 (Baseline Integrity)**: Ensures that FATAL violations are never allowed to be grandfathered into `lint-baseline.xml`.

---

## 🏗️ System Components

### 1. The Canonical Policy ([`ArchitecturalPolicy.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/architecture/src/main/kotlin/com/estatia/realestate/apps/core/architecture/ArchitecturalPolicy.kt))
This object in `:core:architecture` is the **Single Source of Truth**. It defines forbidden packages, layer patterns, and technical debt baselines used by both Konsist and Lint.

### 2. Specialized Law Tests
Tests are organized by law ID in `src/test/kotlin/...`:
- `Law031_LayerMixingTest.kt`: Enforces separation of concerns.
- `Law032_DomainPurityTest.kt`: Guarantees pure Kotlin domain logic.
- `Law034_LintCanaryRegressionTest.kt`: High-fidelity regression verification.
- ... and more.

---

## ⚙️ Usage & Ratchet Policy

### Running Verification
These tests run as standard JUnit tests and are integrated into the PR Gate.
```bash
./gradlew :core:testing-architecture:test
```

### The Ratchet (Continuous Improvement)
If you are refactoring a class that is currently listed in `ArchitecturalPolicy.TechnicalDebt`, you are expected to:
1. Fix the architectural violation.
2. Remove the class from the debt baseline.
3. Verify that the tests still pass.

---

## 🧪 Development
To add a new architectural rule:
1. Update `ArchitecturalPolicy.kt` if the rule involves new forbidden packages or layer definitions.
2. Implement the test using the Konsist API in `ArchitectureConsistencyTest.kt` or `LayerPurityTest.kt`.
3. If the project has existing violations that cannot be fixed immediately, add them to `ArchitecturalPolicy.TechnicalDebt` to baseline them.
