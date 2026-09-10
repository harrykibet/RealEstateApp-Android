# Estatia Architectural Guard - KSP Phase (`:core:ksp-architecture`)

This module provides **High-Precision Compiler Enforcement** for Estatia's most critical architectural laws. It uses Kotlin Symbol Processing (KSP) to intercept compilation and fail the build if structural boundaries are breached.

## 🎯 Purpose

While Android Lint provides real-time feedback in the IDE, `:core:ksp-architecture` acts as the **Final Oracle**. It enforces rules that require 100% precision (e.g., symbol resolution) with zero tolerance for bypass.

---

## 🚦 Actionable Diagnostics

KSP diagnostics in Estatia do more than just report errors; they provide **Guided Refactoring**. Every error follows a structured format:

```text
FATAL Architecture Law: LAW-008 [CRITICAL / CERTAIN]

WHAT: Repository 'PropertyRepository' exposes implementation type 'HashMap'.

WHY: Exposing implementation types couples consumers to internal choices 
     and prevents the swap-ability of underlying infrastructure.

RECOMMENDED: Use interfaces and standard Kotlin collection types 
             (e.g. Map<K, V>) in public signatures.
```

---

## ⚖️ Semantic Enforcement Families

All processors reference the central [`Law`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/architecture/src/main/kotlin/com/estatia/realestate/apps/core/architecture/Law.kt) registry for technical guidance.

### 1. ViewModel Integrity (LAW-018)
- **Problem**: ViewModels becoming "Property Soup" with multiple conflicting state sources.
- **Enforcement**: exactly one canonical UI-state owner (inheriting from `StateFlow`) per ViewModel.
- **Precision**: Uses semantic inheritance matching, allowing custom state wrappers while blocking multiple authorities.

### 2. Abstraction Boundaries (LAW-008)
- **Problem**: Infrastructure types (Retrofit, Room, Firebase) leaking into the domain layer.
- **Enforcement**: Public signatures of repositories/services must only expose domain models or safe primitives.

### 3. State Ownership (LAW-002)
- **Problem**: Exposing mutable state containers (`MutableStateFlow`) to external consumers.
- **Enforcement**: Blocks all public properties using mutable container types.

### 4. Constructor Purity (LAW-030)
- **Problem**: Injecting concrete classes instead of interfaces, hindering test double injection.
- **Enforcement**: Constructors of marked components must only accept interfaces or pure value models.

---

## 🧪 Development & Spec-First Verification

To add a new compiler-enforced rule:
1.  **Define the Law** in `:core:architecture`.
2.  **Create a Modular Spec** in `:core:canary-violations`. Add a `[CANARY:POSITIVE:...]` tag to a failing case and a `[CANARY:NEGATIVE:...]` tag to a passing one.
3.  **Implement the Processor** using the KSP API.
4.  **Verify**: Run `./gradlew :core:canary-violations:ksp...` to ensure the processor identifies the spec correctly.
