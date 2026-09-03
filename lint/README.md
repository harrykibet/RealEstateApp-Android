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
| **LAW-009** | Production functions do not silently discard failures. | `ERROR` | `MissingResultWrapper` |
| **LAW-010** | Sensitive data never enters application logs. | `FATAL` | `SensitiveLogging` |
| **LAW-011** | Shared mutable state requires explicit synchronization. | `FATAL` | `UnsynchronizedChaosState`, `ThreadSafetyViolation` |
| **LAW-012** | Lifecycle-owned work must be cancellable. | `ERROR` | `MissingCoroutineCancellation` |
| **LAW-013** | Critical infrastructure must enforce thread-confinement. | `FATAL` | `MissingConcurrencyCheck` |
| **LAW-014** | Tests must not depend on real time. | `WARNING` | `DirectSystemTimeUsage` |
| **LAW-015** | Tests must strictly remain in test source sets. | `FATAL` | `MockInProduction` |
| **LAW-016** | Mutable state must follow the backing-property convention. | `WARNING` | `BackingPropertyConvention` |
| **LAW-017** | UI components do not own mutable state sources (UDF). | `ERROR` | `MutableStateParameter` |
| **LAW-018** | Suspend functions must not secretly launch independent work. | `FATAL` | `SecretConcurrency` |
| **LAW-019** | Async results (Deferred) must be joined or returned. | `ERROR` | `UnusedAsync` |
| **LAW-020** | Exception handlers must be placed on root scopes. | `WARNING` | `MisplacedCoroutineExceptionHandler` |
| **LAW-021** | UI must remain localized and accessible. | `WARNING` | `HardcodedStringInCompose` |

---

## 🏗️ System Architecture

The engine is structured into specialized layers:

1.  **Architecture Layer**: Enforces module boundaries and dependency direction.
2.  **Concurrency Layer**: Validates thread safety, cancellation, and dispatcher usage.
3.  **API Layer**: Ensures public contracts are safe, immutable, and handle errors explicitly.
4.  **Compose Layer**: Protects the UI layer from business logic leakage and state bugs.
5.  **Security Layer**: Prevents PII leakage and insecure patterns.
6.  **Testing Layer**: Guarantees test infrastructure doesn't leak into production.

---

## 🛠️ Usage for Developers

### Running Locally
To verify your changes before pushing:
```bash
# Run all engineering checks
./gradlew lint
```

### Viewing Reports
Detailed reports with failure explanations and fix suggestions:
`[module-root]/build/reports/lint-results.html`

---

## 🧪 Engine Development
To add a new engineering law or detector:
1.  Define the law in this README.
2.  Add the detector to the appropriate package in `src/main/kotlin/com/estatia/realestate/apps/lint/`.
3.  Register the issue in `EstatiaIssueRegistry`.
4.  **Mandatory**: Add unit tests in `src/test/kotlin/...` to verify the law is correctly enforced.
