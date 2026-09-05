# Estatia Architectural Guard - KSP Phase (`:core:ksp-architecture`)

This module provides **High-Precision Compiler Enforcement** for Estatia's most critical architectural laws. It uses Kotlin Symbol Processing (KSP) to intercept the compilation process and fail the build if fundamental structural rules are violated.

## 🎯 Purpose

While the `:lint` module provides broad, real-time feedback in the IDE, `:core:ksp-architecture` acts as the **final authority**. It handles rules that require 100% precision and zero tolerance for bypass.

### The Hybrid Enforcement Model
- **Level 1 (Lint)**: High-speed, mid-precision rules with IDE highlighting and auto-fixes.
- **Level 2 (Konsist)**: Global structural tests (module graph, naming conventions).
- **Level 3 (KSP)**: Semantic, compile-time errors for "mission-critical" architectural boundaries.

---

## ⚖️ Enforced Laws

### 1. Mandatory Result Wrapping (LAW-009)
- **Problem**: Public methods in repositories or services returning raw implementation types allow failures to be discarded silently.
- **Enforcement**: Classes annotated with `@Repository`, `@Service`, or `@UseCase` must return `AppResult<T>`, `Flow<T>`, or `Unit`.
- **Processor**: `ResultWrappingProcessor`

### 2. Contractual Consistency (LAW-008)
- **Problem**: Direct implementation leakage.
- **Enforcement**: Every class annotated with `@Repository` or `@UseCase` **must** implement an interface.
- **Processor**: `ContractProcessor`

### 3. Abstraction Boundaries (LAW-008)
- **Problem**: Domain components leaking infrastructure types (e.g., Firebase, Room, OkHttp).
- **Enforcement**: Verifies that public APIs of `@UseCase` and `@Repository` do not expose infrastructure types.
- **Processor**: `AbstractionBoundaryProcessor`

### 4. Constructor Purity (LAW-030)
- **Problem**: Injecting concrete implementations instead of abstractions.
- **Enforcement**: Primary constructors of architectural components must only accept interfaces (starting with 'I') or pure Data Models.
- **Processor**: `ConstructorAbstractionProcessor`

### 5. ViewModel Integrity (LAW-018 & LAW-016)
- **Problem**: "Property soup" (multiple StateFlows) and mutable state leakage.
- **Enforcement**:
    - Exactly one public `StateFlow` allowed per `@ViewModelMarker` (Single Source of Truth).
    - Zero public mutable containers allowed (`MutableStateFlow`, `MutableState`).
- **Processor**: `ViewModelProcessor`

### 6. Domain Expressiveness (LAW-008)
- **Problem**: Returning raw `Boolean` or `Int` in `AppResult` obscures business meaning.
- **Enforcement**: Warns when UseCases return primitives, encouraging enums or sealed classes.
- **Processor**: `ContractProcessor`

---

## 🛠️ Usage

### 1. Annotate your component
Mark your class with the appropriate architectural annotation from `:core:common`:

```kotlin
import com.estatia.realestate.apps.core.common.annotations.Repository

@Repository
class PropertyRepositoryImpl(...) : IPropertyRepository {
    // KSP will verify every public function here
}
```

### 2. Apply the processor
Add the processor to the module's `build.gradle.kts`:

```kotlin
dependencies {
    ksp(project(":core:ksp-architecture"))
}
```

---

## 🧪 Development
To add a new architectural rule:
1. Define a marker annotation in `:core:common`.
2. Create a new `SymbolProcessor` class in `src/main/kotlin`.
3. Register the `SymbolProcessorProvider` in `META-INF/services/com.google.devtools.ksp.processing.SymbolProcessorProvider`.
