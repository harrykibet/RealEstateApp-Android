# Estatia Engineering Guard (`:lint`)

This module houses the **Estatia Engineering Rules Engine**. Its job is to make bad architecture, unsafe concurrency, leaky abstractions, and production-hostile implementation patterns hard or impossible to merge.

## 🏗️ Core Philosophy
In Estatia, we treat architectural principles as **Compiler-Enforced Laws**, not optional style advice. This system acts as a non-bypassable gate in our CI/CD pipeline.

---

## 🚦 Enforcement Levels (Severity Model)

We use a tiered severity model based on production risk to ensure that developers focus on critical issues without being overwhelmed by minor suggestions.

| Tier | Severity | Policy |
| :--- | :--- | :--- |
| **FATAL** | `FATAL` | Fundamental structural rules or security risks that **must never enter main**. CI will fail and blocks merge. |
| **ERROR** | `ERROR` | Core production safety, concurrency, or lifecycle defects. Must be resolved before release. |
| **WARNING** | `WARNING` | Design smells or localized best-practice deviations. Requires justification if suppressed. |
| **CONVENTION** | `WARNING` | Estatia-specific design patterns that promote consistency but aren't structural laws. |
| **STYLE** | `INFORMATIONAL` | Syntactic or organizational style preferences. Recommended for clarity. |
| **INFO** | `INFORMATIONAL` | Optimization or future-proofing guidance. Optional. |

---

## ⚖️ The Laws of the Codebase

Every detector in this module enforces a rule defined in the central [`Law`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/architecture/src/main/kotlin/com/estatia/realestate/apps/core/architecture/Law.kt) registry. 

**Legend**: `L:` Lint Rule, `K:` KSP Processor, `S:` Konsist Test, `T:` Gradle Task, `V:` Verification/Test.

| Law ID | Law Description | Type | Enforcement Rule(s) |
| :--- | :--- | :--- | :--- |
| **LAW-001** | Presentation owns UI state. | `CONVENTION` | `L:BusinessLogicInCompose`, `L:MagicNumber` |
| **LAW-002** | Mutable state never crosses an ownership boundary. | `ERROR` | `L:ExposedMutableState`, `K:Law002_ExposedMutableState`, `L:RememberMissing`, `L:MutableStateParameter`, `S:Law008_Law002_PublicApiPurity` |
| **LAW-003** | Infrastructure does not leak into domain or presentation. | `FATAL` | `L:InfrastructureLeakage`, `S:Law003_FeatureIsolation` |
| **LAW-004** | Feature modules cannot depend on other feature modules. | `FATAL` | `L:FeatureCouplingViolation`, `S:Law004_NamingConsistency` |
| **LAW-005** | Production code does not create coroutine scopes. | `FATAL` | `L:ForbiddenCoroutineScope` |
| **LAW-006** | Production code does not choose dispatchers directly. | `FATAL` | `L:HardcodedDispatcher` |
| **LAW-007** | Production code does not use wall-clock time directly. | `ERROR` | `L:DirectSystemTimeUsage` |
| **LAW-008** | Public APIs expose abstractions, not implementation types. | `FATAL` | `L:MissingVisibilityModifier`, `L:ImplementationTypeInPublicApi`, `K:Law008_InterfaceContract`, `K:Law008_AbstractionLeakage`, `S:Law008_Law002_PublicApiPurity` |
| **LAW-009** | Production functions do not silently discard failures. | `CONVENTION` | `L:MissingResultWrapper`, `K:Law009_ResultWrapping`, `L:FailureSmuggling`, `L:DangerousFallback` |
| **LAW-010** | Sensitive data never enters application logs. | `FATAL` | `L:SensitiveLogging`, `L:HardcodedSecrets` |
| **LAW-011** | Blocking work never executes on the main thread. | `FATAL` | `L:BlockingMainThreadWork`, `L:UnboundedBuffer` |
| **LAW-012** | Shared mutable state requires explicit synchronization. | `FATAL` | `L:UnsynchronizedChaosState`, `L:ThreadSafetyViolation`, `L:UnsafeStateCollection` |
| **LAW-013** | Lifecycle-owned work must be cancellable. | `ERROR` | `L:MissingCoroutineCancellation` |
| **LAW-014** | Critical infrastructure must enforce thread-confinement. | `FATAL` | `L:MissingConcurrencyCheck` |
| **LAW-015** | Tests must not depend on real time. | `WARNING` | `L:DirectSystemTimeUsageInTest` |
| **LAW-016** | Tests must strictly remain in test source sets. | `FATAL` | `L:MockInProduction` |
| **LAW-017** | Mutable state must follow the backing-property convention. | `STYLE` | `L:BackingPropertyConvention` |
| **LAW-018** | ViewModels must expose a single canonical persistent UI-state owner. | `ERROR` | `K:Law018_ViewModelSsot` |
| **LAW-019** | Suspend functions must not secretly launch independent work. | `FATAL` | `L:SecretConcurrency` |
| **LAW-020** | Async results (Deferred) must be joined or returned. | `ERROR` | `L:UnusedAsync` |
| **LAW-021** | Exception handlers must be placed on root scopes. | `WARNING` | `L:MisplacedCoroutineExceptionHandler` |
| **LAW-022** | UI must remain localized and accessible. | `CONVENTION` | `L:HardcodedStringInCompose`, `L:DesignSystemViolation`, `L:HardcodedDesignValue` |
| **LAW-023** | Lifecycle-bound objects (Activity/View) must not be stored in long-lived components. | `FATAL` | `L:LifecycleLeak` |
| **LAW-024** | Long-lived components must not hold direct references to UI Context. | `FATAL` | `L:ContextLeak` |
| **LAW-025** | Composables must not read from mutable singletons directly. | `ERROR` | `L:ComposeMutableSingletonRead` |
| **LAW-026** | Expensive object creation must be cached via remember. | `WARNING` | `L:ExpensiveRecomposition` |
| **LAW-027** | Composables must not directly call domain or data layer components. | `ERROR` | `L:ComposeArchitectureLeakage` |
| **LAW-028** | Methods must be concise and focused (Complexity Budget). | `FATAL` | `L:SpaghettiMethodFatal` |
| **LAW-029** | Classes must have a single responsibility (Size Limit). | `FATAL` | `L:GodObjectFatal` |
| **LAW-030** | Constructors must have a limited dependency budget. | `ERROR` | `L:OrchestrationMonsterError`, `K:Law030_ConstructorPurity` |
| **LAW-031** | Components must not mix architectural layers or responsibilities. | `FATAL` | `S:Law031_LayerMixing` |
| **LAW-032** | Domain and Model layers must remain pure Kotlin (No Frameworks). | `FATAL` | `S:Law032_DomainPurity` |
| **LAW-033** | Rule suppressions must follow strict organizational policy. | `FATAL` | `L:SuppressionPolicyViolation`, `S:Law033_GradleSuppression` |
| **LAW-034** | Architectural enforcement must be crash-resilient and regression-tested. | `FATAL` | `L:LintCanaryActive`, `V:Law034_LintCanaryRegression` |
| **LAW-035** | FATAL architectural rules must never be baselined. | `FATAL` | `V:Law035_FatalBaselineIntegrity` |
| **LAW-036** | Domain results should express business meaning via types. | `CONVENTION` | `K:Law036_DomainExpressiveness` |
| **LAW-037** | Dependencies must be managed via the version catalog. | `FATAL` | `T:checkDependencyDrift` |
| **LAW-038** | Production binaries must remain pure and obfuscated. | `FATAL` | `T:auditBinaryPurity` |

---

## 📈 Complexity Budgets

To prevent technical debt, we enforce tiered thresholds for code complexity.

### Method Complexity (LAW-028)
We enforce a strict complexity budget to keep code testable and readable.
- **Complexity**: Cyclomatic Complexity (number of decision branches: `if`, `for`, `when`, `&&`, `||`, `catch`, etc.).
- **Length**: Physical lines of code in the method body.
- **Nesting Depth**: Maximum depth of nested control structures.
- **Fan-out**: Number of unique call sites (external dependencies/calls).

| Metric | WARNING | ERROR | FATAL |
| :--- | :--- | :--- | :--- |
| **Complexity** | > 10 | > 20 | > 30 |
| **Length** | > 60 lines | > 120 lines | > 300 lines |
| **Nesting Depth** | > 3 | > 5 | > 8 |
| **Fan-out** | > 10 | > 20 | > 40 |

### Class Size & Scope (LAW-029)
- **Size**: Total lines of code in the class.
- **Public Surface Area**: Total count of public methods and properties.
- **Mutable State**: Total count of `var` properties or `MutableState`/`Flow` containers.

| Metric | WARNING | ERROR | FATAL |
| :--- | :--- | :--- | :--- |
| **Size** | > 300 lines | > 600 lines | > 1000 lines |
| **Public Surface Area** | > 15 | > 25 | > 40 |
| **Mutable State** | > 5 | > 8 | > 12 |

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

### Customizing Thresholds (Per-Module)
You can override default complexity and logic thresholds by adding a `lint.xml` file to your module:

```xml
<lint>
    <!-- Stricter dependency budget for critical engine -->
    <issue id="OrchestrationMonsterError">
        <option name="errorThreshold" value="5" />
        <option name="warningThreshold" value="3" />
    </issue>
    
    <!-- Custom allowed numbers for specific domains -->
    <issue id="MagicNumber">
        <option name="allowedNumbers" value="21,42,1337" />
    </issue>
</lint>
```

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
