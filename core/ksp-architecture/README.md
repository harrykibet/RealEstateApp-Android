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

### LAW-009: Mandatory Result Wrapping
**Problem**: Public methods in repositories or services returning raw implementation types (e.g., `User`, `List<Property>`) allow failures to be discarded silently.
**Enforcement**: 
- Any class annotated with `@Repository`, `@Service`, or `@UseCase` is inspected.
- All public methods **must** return a wrapped type: `AppResult<T>`, `Flow<T>`, or `Unit`.
- **Compile Error**: "Architecture Violation (LAW-009): Public method ... must return a wrapped Result type."

### LAW-008: Abstraction Boundaries
**Problem**: Domain components leaking infrastructure types (e.g., Firebase classes, Room Entities, OkHttp types).
**Enforcement**: 
- Verifies that return types and parameters of `@UseCase` and `@Repository` components do not expose types from `com.google.firebase`, `androidx.room`, `okhttp3`, or `retrofit2`.
- **Compile Error**: "Architecture Violation (LAW-008): Leakage detected in ... Public parameter/return type exposes infrastructure type."

### LAW-016: ViewModel State Ownership
**Problem**: Exposing `MutableStateFlow` or `MutableState` from ViewModels, which allows the View to mutate state directly.
**Enforcement**: 
- Ensures all public properties in classes marked with `@ViewModelMarker` are read-only abstractions.
- **Compile Error**: "Architecture Violation (LAW-016): ViewModel ... exposes mutable state ... Expose as StateFlow instead."

---

## 🛠️ Usage

### 1. Annotate your component
Mark your class with the appropriate architectural annotation from `:core:common`:

```kotlin
import com.estatia.realestate.apps.core.common.annotations.Repository

@Repository
class PropertyRepositoryImpl(...) : PropertyRepository {
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
2. Update `ArchitectureProcessor.kt` in this module with the new validation logic.
