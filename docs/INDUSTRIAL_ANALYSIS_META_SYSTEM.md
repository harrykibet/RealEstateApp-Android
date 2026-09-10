# Estatia — Industrial Analysis Meta-System

## Overview

The **Industrial Analysis Meta-System** is Estatia's automated governance engine. Unlike standard static analysis which relies on naming conventions and "best-effort" heuristics, Estatia uses a **Semantic-First** approach. This system ensures that 100% of the functional codebase is governed by architectural laws, leaving no room for "Shadow Layers" or accidental bypasses.

## The Core Constraint: Mandatory Architectural Identity (LAW-041)

The foundation of this system is the **Identity Mandate**. Every top-level functional component (class, object, or interface) in governed modules **must** explicitly declare its architectural role via a recognized annotation.

### Rationale: Trading DX for Precision

The decision to mandate annotations was a deliberate engineering trade-off. While it adds a minor step to component creation (reduced Developer Experience), it provides industrial-grade benefits to the system's safety and maintainability.

#### 1. Deterministic Precision (Certainty vs. Guesswork)
Standard linting often uses heuristics like `name.endsWith("Repository")`. This is fragile:
- **False Negatives**: A developer could create `internal class SecretDataStore` and bypass every repository-specific safety check (like Result wrapping or thread-safety).
- **False Positives**: A utility class named `RepositoryUtils` might be incorrectly flagged for violating repository laws.

By mandating annotations, we move from **Heuristic** (Guessing) to **Certain** (Authoritative) resolution. The analyzer knows *exactly* what a class is because the developer explicitly declared it.

#### 2. Analysis Efficiency (The Cost of Inference)
Trying to "understand" the purpose of a class by scanning its methods, imports, and logic is computationally expensive and error-prone. 
- **Scale**: As the project grows to 1,000+ modules, deep control-flow analysis would make CI build times explode.
- **Speed**: Annotation scanning is a constant-time O(1) operation during UAST/KSP processing, allowing our safety gates to remain near-instant.

#### 3. Total Layer Accountability
In many architectures, `internal` or `private` classes are "invisible" to governance. In Estatia, visibility is for encapsulation, but annotations are for accountability. This ensures that even internal logic helpers are audited for:
- **Dependency Purity** (No framework leakage).
- **Thread Safety** (Mandatory synchronization in shared roles).
- **Error Handling** (Mandated Result wrapping at the boundary).

---

## The Semantic Taxonomy

Estatia uses a rich toolkit of specific roles to provide high-fidelity diagnostics.

| Role | Target | Key Law Enabled |
| :--- | :--- | :--- |
| **`@Contract`** | Interface | Enforces safety rules (Result wrapping) at the behavioral boundary. |
| **`@Repository`** | Class | Enforces data source isolation and mandatory error handling. |
| **`@Service`** | Class | Enforces layer-agnostic business logic standards. |
| **`@UseCase`** | Class | Enforces constructor purity and domain-only dependencies. |
| **`@DataSource`** | Class | Identifies IO-heavy components for mandatory thread-confinement. |
| **`@DomainModel`** | Data Class | Enforces **LAW-032** (Pure Kotlin only, no Frameworks). |
| **`@EntityModel`** | Data Class | Prevents leakage of DB/Network schemas into UI layers. |
| **`@Manager`** | Class | Enforces strict synchronization on state-holding singletons. |
| **`@Helper`** | Class | Audits generic logic utilities for code health and complexity. |
| **`@AppEntryPoint`**| Class | Identifies framework roots (Activities, Application). |

---

## Enforcement Mechanics

Estatia employs a multi-layered mechanical gate to ensure the mandate is followed:

1.  **Konsist Layer (`LAW-041`)**: A structural test that scans the entire project. It will **BLOCK** any merge if a top-level functional class or interface is found without a valid architectural identity.
2.  **Lint Layer**: Provides real-time feedback in the IDE. Detectors (like `ExposedMutableStateDetector`) now have 100% visibility over all governed components.
3.  **KSP Layer**: Performs deep signature audits during compilation (e.g., ensuring every method in a `@Contract` returns an `AppResult`).

## Exceptions & Exclusions

To maintain focus on logic and behavior, the following types are exempt from the mandatory annotation policy:
- **Enums**: Intrinsically passive constants.
- **Annotation Classes**: Metadata definitions.
- **Generated Code**: (e.g., Room `_Impl` classes, Dagger factories).
- **Local/Inner Classes**: These inherit the architectural identity of their parent container.

---

> [!IMPORTANT]
> **Declaration of Intent is Mandatory.**
> If you create a new logical component, the system will not guess its purpose. You must declare it. This is the contract that guarantees the safety of the entire Estatia ecosystem.
