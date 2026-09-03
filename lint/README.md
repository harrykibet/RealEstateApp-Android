# Estatia Engineering Guard (`:lint`)

This module houses the **Estatia Engineering Rules Engine**. Its job is to make bad architecture, unsafe concurrency, leaky abstractions, and production-hostile implementation patterns hard or impossible to merge.

## 🏗️ Core Philosophy
In Estatia, we treat architectural principles as **Compiler-Enforced Laws**, not optional style advice. This system acts as a non-bypassable gate in our CI/CD pipeline.

---

## 🚦 Enforcement Levels (Severity Model)

We use a tiered severity model based on production risk to ensure that developers focus on critical issues without being overwhelmed by minor suggestions.

| Tier | Severity | Policy |
| :--- | :--- | :--- |
| **FATAL** | `FATAL` | Architectural violations or security risks that **must never enter main**. CI will fail and blocks merge. |
| **ERROR** | `ERROR` | Production correctness, concurrency, or lifecycle defects. Must be fixed before release. |
| **WARNING** | `WARNING` | Strong design smells or localized best-practice violations. Requires justification if suppressed. |
| **INFO** | `INFORMATIONAL` | Optimization or maintainability guidance. Recommended but optional. |

---

## ⚖️ The Laws of the Codebase

Every detector in this module exists to enforce one of the following fundamental engineering laws:

| Law ID | Law Description | Level | Enforcing Rule |
| :--- | :--- | :--- | :--- |
| **LAW-001** | Presentation owns UI state. | `WARNING` | `BusinessLogicInCompose` |
| **LAW-002** | Mutable state never crosses an ownership boundary. | `ERROR` | `ExposedMutableState` |
| **LAW-003** | Infrastructure does not leak into domain or presentation. | `FATAL` | `InfrastructureLeakage` |
| **LAW-004** | Feature modules cannot depend on other feature modules. | `FATAL` | `FeatureCouplingViolation` |
| **LAW-005** | Production code does not create coroutine scopes. | `FATAL` | `ForbiddenCoroutineScope` |
| **LAW-006** | Production code does not choose dispatchers directly. | `FATAL` | `HardcodedDispatcher` |
| **LAW-007** | Production code does not use wall-clock time directly. | `ERROR` | `DirectSystemTimeUsage` |
| **LAW-008** | Public APIs expose abstractions, not implementation types. | `FATAL` | `ImplementationTypeInPublicApi` |
| **LAW-009** | Production functions do not silently discard failures. | `ERROR` | `MissingResultWrapper`, `FailureSmuggling`, `DangerousFallback` |
| **LAW-010** | Sensitive data never enters application logs. | `FATAL` | `SensitiveLogging` |
| **LAW-011** | Blocking work never executes on the main thread. | `FATAL` | `BlockingMainThreadWork` |
| **LAW-012** | Shared mutable state requires explicit synchronization. | `FATAL` | `UnsynchronizedChaosState`, `ThreadSafetyViolation` |
| **LAW-013** | Lifecycle-owned work must be cancellable. | `ERROR` | `MissingCoroutineCancellation` |
| **LAW-014** | Critical infrastructure must enforce thread-confinement. | `FATAL` | `MissingConcurrencyCheck` |
| **LAW-015** | Tests must not depend on real time. | `WARNING` | `DirectSystemTimeUsage` |
| **LAW-016** | Tests must strictly remain in test source sets. | `FATAL` | `MockInProduction` |
| **LAW-017** | Mutable state must follow the backing-property convention. | `WARNING` | `BackingPropertyConvention` |
| **LAW-018** | UI components do not own mutable state sources (UDF). | `ERROR` | `MutableStateParameter` |
| **LAW-019** | Suspend functions must not secretly launch independent work. | `FATAL` | `SecretConcurrency` |
| **LAW-020** | Async results (Deferred) must be joined or returned. | `ERROR` | `UnusedAsync` |
| **LAW-021** | Exception handlers must be placed on root scopes. | `WARNING` | `MisplacedCoroutineExceptionHandler` |
| **LAW-022** | UI must remain localized and accessible. | `WARNING` | `HardcodedStringInCompose` |
| **LAW-023** | Lifecycle-bound objects (Activity/View) must not be stored in long-lived components. | `FATAL` | `LifecycleLeak` |
| **LAW-024** | Long-lived components must not hold direct references to UI Context. | `FATAL` | `LifecycleLeak` |
| **LAW-025** | Composables must not read from mutable singletons directly. | `ERROR` | `ComposeMutableSingletonRead` |
| **LAW-026** | Expensive object creation must be cached via remember. | `WARNING` | `ExpensiveRecomposition` |
| **LAW-027** | Composables must not directly call domain or data layer components. | `ERROR` | `ComposeArchitectureLeakage` |
| **LAW-028** | Methods must be concise and focused (Complexity Budget). | `FATAL` | `SpaghettiMethodFatal` |
| **LAW-029** | Classes must have a single responsibility (Size Limit). | `FATAL` | `GodObjectFatal` |
| **LAW-030** | Constructors must have a limited dependency budget. | `ERROR` | `OrchestrationMonsterError` |
| **LAW-031** | Components must not mix architectural layers or responsibilities. | `FATAL` | `LayerMixingViolation` |
| **LAW-032** | Domain and Model layers must remain pure Kotlin (No Frameworks). | `FATAL` | `LayerDependencyViolation` |
| **LAW-033** | Rule suppressions must follow strict organizational policy. | `FATAL` | `SuppressionPolicyViolation` |

---

## 📈 Complexity Budgets

To prevent technical debt, we enforce tiered thresholds for code complexity.

### Class Size (LAW-029)
- **> 300 lines**: `WARNING` (Design smell)
- **> 600 lines**: `ERROR` (Must refactor)
- **> 1000 lines**: `FATAL` (Merge blocked)

### Method Size (LAW-028)
- **> 60 lines**: `WARNING` (Lengthy function)
- **> 120 lines**: `ERROR` (Complex logic)
- **> 300 lines**: `FATAL` (Merge blocked)

### Parameters
- **> 7 params**: `WARNING`
- **> 12 params**: `ERROR`

### Constructor Dependencies (LAW-030)
- **1–5 dependencies**: Normal (Healthy decoupling)
- **6–8 dependencies**: `WARNING` (Design smell: Orchestration monster)
- **9+ dependencies**: `ERROR` (Refactor now: Too many responsibilities)

---

## 🛡️ Multi-Layered Enforcement

Estatia uses the "Right Tool for the Job" for enforcement:

| Layer | Responsibility | Tool |
| :--- | :--- | :--- |
| **Build Configuration** | Module Isolation (LAW-004) | Gradle (Convention Plugins) |
| **Architectural Scope** | Package Purity (LAW-032) | Konsist (Architecture Tests) |
| **Implementation Guard** | Complex Android/Compose Semantic Patterns | Android Lint (Custom Detectors) |
| **Code Style** | Formatting & Basic Smells | Ktlint / Detekt |
| **Policy Authority** | PR Gating & Suppression Governance | GitHub Actions |

---

## ⚙️ The Ratchet Policy (Continuous Improvement)

To prevent technical debt from accumulating while allowing work on the existing codebase, Estatia uses a **Lint Ratchet**.

### 1. Grandfathering (Baseline)
Existing violations are stored in `lint-baseline.xml`. These are "grandfathered" and do not fail the build.

### 2. No New Violations
Any **new** code that introduces a lint violation will fail the build immediately. The build state must always be "Same or Better" than the baseline.

### 3. Tightening the Ratchet
As you fix existing issues, the baseline should be updated. Our goal is to reach a zero-violation state.
- **Weekly Goal**: Reduce baseline issue count by ~10%.
- **PR Requirement**: If you touch a file with existing violations, you are encouraged to fix at least one and update the baseline.

### 4. Updating the Baseline
To update the baseline after fixing issues:
```bash
./gradlew lint -Dlint.update.baseline=true
```
Submit the updated `lint-baseline.xml` as part of your PR.

---

## 🏗️ System Architecture

The engine is structured into specialized layers:

1.  **Architecture Layer**: Enforces module boundaries and dependency direction.
2.  **Concurrency Layer**: Validates thread safety, cancellation, and dispatcher usage.
3.  **API Layer**: Ensures public contracts are safe, immutable, and handle errors explicitly.
4.  **Compose Layer**: Protects the UI layer from business logic leakage and state bugs.
5.  **Security Layer**: Prevents PII leakage and insecure patterns.
6.  **Performance Layer**: Blocks main-thread work and unbounded memory growth.
7.  **Testing Layer**: Guarantees test infrastructure doesn't leak into production.

---

## 🏗️ The CI Authority Model

The Estatia CI pipeline is the final word on engineering quality. It is structured to provide fast feedback for developers while maintaining high-rigor checks nightly.

### 1. PR Gate (Fast Feedback)
Every Pull Request must pass the following sequence:
- **Architecture & Policy**: Enforces all 33+ Lint Laws and the Ratchet.
- **Logic Verification**: Runs all unit tests and verifies the coverage ratchet.
- **Smoke Instrumentation**: Fast UI tests on a small set of virtual devices (GMD).

### 2. Main Branch (Independence)
The `main` branch is independently verified. No code enters `main` without passing release-grade verification.

### 3. Nightly Run (Stress & Optimization)
Comprehensive checks run every 24 hours:
- **Chaos Testing**: Gesture and network chaos to find non-deterministic crashes.
- **Macrobenchmarks**: Verifies app startup and scrolling performance.
- **Baseline Profiles**: Automated optimization of release artifacts.
- **Full Coverage**: Detailed Jacoco reports for entire modules.
- **Security Scan**: OWASP Dependency Check for CVEs in the dependency tree.

---

## 🧪 Engine Development
To add a new engineering law or detector:
1.  Define the law in this README.
2.  Add the detector to the appropriate package in `src/main/kotlin/com/estatia/realestate/apps/lint/`.
3.  Register the issue in `EstatiaIssueRegistry`.
4.  **Mandatory**: Add unit tests in `src/test/kotlin/...` to verify the law is correctly enforced.
