# Estatia Architectural Guard - Konsist Phase (`:core:testing-architecture`)

This module houses the **Architectural Source of Truth** and global structural verification tests for the Estatia project. It leverages **Konsist** to perform deep static analysis across the entire project graph.

## 🎯 Purpose

While Android Lint focuses on local file-level patterns and KSP enforces high-precision semantic rules during compilation, `:core:testing-architecture` provides **Level 2 (Global Structural)** enforcement. It ensures that the project's macro-architecture (module boundaries, layer purity, and naming conventions) remains intact as the codebase grows.

### The Hybrid Enforcement Model
- **Level 1 (Lint)**: Fast, IDE-integrated feedback for common coding mistakes.
- **Level 2 (Konsist)**: Global structural tests that view the project as a single graph.
- **Level 3 (KSP)**: 100% precise compiler errors for mission-critical architectural boundaries.

---

## ⚖️ Enforced Laws

The following laws (as defined in the root `lint/README.md`) are primary targets for Konsist enforcement:

### Layer Purity & Isolation
- **LAW-032 (Pure Domain/Model)**: Enforces that `:core:domain` and `:core:model` remain pure Kotlin/Java, strictly forbidding dependencies on Android Frameworks or infrastructure libraries (Firebase, Room, OkHttp).
- **LAW-003 (Feature Isolation)**: Prevents feature modules from depending on other feature modules (except for shared utilities) or direct infrastructure implementations.
- **LAW-031 (Layer Mixing)**: Ensures that business logic components (Repositories, UseCases) and ViewModels do not reference UI frameworks like Compose or Android Views.

### API & State Integrity
- **LAW-008 / LAW-016 (Public API Purity)**: Scans all public properties and functions to ensure they do not expose mutable containers (e.g., `MutableStateFlow`, `ArrayList`) or implementation-specific types.
- **LAW-004 (Naming Consistency)**: Validates that package names strictly follow the module structure (e.g., code in `:feature:home` must reside in `com.estatia.realestate.apps.feature.home`).

### Complexity Budgets
- **LAW-029 (Class Size)**: Blocks classes exceeding 1000 lines (FATAL).
- **LAW-028 (Method Size)**: Blocks functions exceeding 300 lines (FATAL).
- **LAW-030 (Dependency Budget)**: Blocks constructors with more than 8 dependencies to prevent "Orchestration Monsters."

---

## 🏗️ System Components

### 1. The Canonical Policy ([`ArchitecturalPolicy.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/testing-architecture/src/test/kotlin/com/estatia/realestate/apps/core/testing_architecture/ArchitecturalPolicy.kt))
This object is the **Single Source of Truth**. It defines:
- **Forbidden Packages**: Centralized list of infrastructure/UI libraries.
- **Layer Definitions**: Package patterns for Domain, Model, Feature, etc.
- **Technical Debt Baseline**: Explicit list of files currently exempt from certain rules to keep CI green while preventing *new* leaks.

### 2. Consistency Tests ([`ArchitectureConsistencyTest.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/testing-architecture/src/test/kotlin/com/estatia/realestate/apps/core/testing_architecture/ArchitectureConsistencyTest.kt))
General rules applied across the whole project, including naming conventions and complexity budgets.

### 3. Purity Tests ([`LayerPurityTest.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/testing-architecture/src/test/kotlin/com/estatia/realestate/apps/core/testing_architecture/LayerPurityTest.kt))
Specific tests for ensuring that architectural layers (especially Domain and Model) remain decoupled from implementation details.

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
