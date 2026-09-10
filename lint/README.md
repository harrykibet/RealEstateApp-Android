# Estatia Engineering Guard (`:lint`)

This module houses the **Estatia Engineering Rules Engine**. Its job is to make bad architecture, unsafe concurrency, leaky abstractions, and production-hostile implementation patterns hard or impossible to merge.

## 🏗️ Core Philosophy
In Estatia, we treat architectural principles as **Compiler-Enforced Laws**, not optional style advice. This system acts as a non-bypassable gate in our CI/CD pipeline.

---

## 🚦 The Enforcement Oracle (3D Risk Model)

Estatia uses a multi-dimensional model to evaluate and enforce architectural laws.

| Dimension | Levels | Description |
| :--- | :--- | :--- |
| **Risk** | `CRITICAL`, `HIGH`, `MEDIUM`, `LOW` | Impact on production stability, security, or maintainability. |
| **Confidence** | `CERTAIN`, `HIGH`, `HEURISTIC` | Technical certainty of the detection mechanism. |
| **Enforcement** | `BLOCK`, `WARN`, `INFO` | The action taken by the CI pipeline and IDE. |

---

## 🚦 Actionable Diagnostics

Diagnostics in the Estatia system are designed to be **Engineering Assistants**. Every report answers:

- **WHAT**: The specific technical violation detected.
- **WHY**: The underlying architectural rationale and production risk.
- **HOW**: Authoritative recommended steps for resolution.
- **METADATA**: Real-time evaluation of Risk and Confidence.

---

## ⚖️ The Laws of the Codebase

Every detector in this module enforces a rule defined in the central [`Law`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/architecture/src/main/kotlin/com/estatia/realestate/apps/core/architecture/Law.kt) registry. 

### Enforcement Legend
| Code | Confidence | Implementation Detail |
| :--- | :--- | :--- |
| **L:** | `CERTAIN` | Semantic analysis via UAST & Symbol resolution. |
| **K:** | `CERTAIN` | Compiler-integrated symbol processing. |
| **S:** | `HIGH` | Import-string and topology analysis (Konsist). |
| **H:** | `HEURISTIC` | Pattern/Keyword matching (Syntactic Lint/Grep). |
| **V:** | `CERTAIN` | High-fidelity verification tests. |

### 🏗️ Architecture & Boundaries
| Law ID | Law Description | Risk | Confidence | Enforcement | Enforcement Rule(s) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **LAW-002** | Mutable state ownership boundary. | `HIGH` | `CERTAIN` | `BLOCK` | `L:ExposedMutableState`, `K:Law002_ExposedMutableStateProcessor`, `L:RememberMissing`, `L:MutableStateParameter` |
| **LAW-003** | Infrastructure leakage prevention. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:InfrastructureLeakage`, `S:Law003_FeatureIsolationTest` |
| **LAW-004** | Feature module isolation. | `HIGH` | `HIGH` | `BLOCK` | `L:FeatureCouplingViolation`, `S:Law004_NamingConsistencyTest` |
| **LAW-016** | Test infrastructure isolation. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:MockInProduction` |
| **LAW-031** | Cross-layer concern separation. | `CRITICAL` | `HIGH` | `BLOCK` | `S:Law031_LayerMixingTest` |
| **LAW-032** | Domain/Model pure-Kotlin mandate. | `CRITICAL` | `HIGH` | `BLOCK` | `S:Law032_DomainPurityTest` |

### 🏎️ Concurrency & Thread Safety
| Law ID | Law Description | Risk | Confidence | Enforcement | Enforcement Rule(s) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **LAW-005** | No manual CoroutineScopes. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:ForbiddenCoroutineScope` |
| **LAW-006** | No hardcoded Dispatchers. | `HIGH` | `CERTAIN` | `BLOCK` | `L:HardcodedDispatcher` |
| **LAW-012** | Shared mutable state synchronization. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:UnsynchronizedChaosState`, `L:ThreadSafetyViolation`, `L:UnsafeStateCollection` |
| **LAW-013** | Cooperative cancellation. | `HIGH` | `CERTAIN` | `BLOCK` | `L:MissingCoroutineCancellation` |
| **LAW-014** | Thread-confinement enforcement. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:MissingConcurrencyCheck` |
| **LAW-019** | Sequential suspend functions. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:SecretConcurrency` |
| **LAW-020** | Mandatory Deferred joining. | `HIGH` | `CERTAIN` | `BLOCK` | `L:UnusedAsync` |
| **LAW-021** | Correct CEH placement. | `MEDIUM` | `CERTAIN` | `WARN` | `L:MisplacedCoroutineExceptionHandler` |

### 🛠️ API & State Design
| Law ID | Law Description | Risk | Confidence | Enforcement | Enforcement Rule(s) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **LAW-008** | Abstraction-only public APIs. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:MissingVisibilityModifier`, `L:ImplementationTypeInPublicApi`, `K:Law008_InterfaceContractProcessor`, `K:Law008_AbstractionLeakageProcessor` |
| **LAW-009** | Explicit failure handling. | `MEDIUM` | `HEURISTIC` | `WARN` | `L:MissingResultWrapper`, `K:Law009_ResultWrappingProcessor`, `L:FailureSmuggling`, `L:DangerousFallback` |
| **LAW-018** | ViewModel Single Source of Truth. | `HIGH` | `CERTAIN` | `BLOCK` | `K:Law018_ViewModelSsotProcessor` |
| **LAW-036** | Rich-type domain results. | `MEDIUM` | `CERTAIN` | `WARN` | `K:Law036_DomainExpressivenessProcessor` |

### 🎨 UI & Design Governance
| Law ID | Law Description | Risk | Confidence | Enforcement | Enforcement Rule(s) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **LAW-001** | Presentation-owned UI state. | `MEDIUM` | `HEURISTIC` | `WARN` | `L:BusinessLogicInCompose` |
| **LAW-022** | Localization & Design consistency. | `MEDIUM` | `HEURISTIC` | `WARN` | `L:HardcodedStringInCompose`, `L:DesignSystemViolation`, `L:HardcodedDesignValue` |
| **LAW-025** | No mutable singleton reads in UI. | `HIGH` | `CERTAIN` | `BLOCK` | `L:ComposeMutableSingletonRead` |
| **LAW-027** | UDF: No direct data layer calls. | `HIGH` | `CERTAIN` | `BLOCK` | `L:ComposeArchitectureLeakage` |

### ⚡ Performance & Memory
| Law ID | Law Description | Risk | Confidence | Enforcement | Enforcement Rule(s) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **LAW-007** | Clock-injected wall time. | `MEDIUM` | `CERTAIN` | `WARN` | `L:DirectSystemTimeUsage` |
| **LAW-011** | Main-thread safety. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:BlockingMainThreadWork`, `L:UnboundedBuffer` |
| **LAW-015** | Tests must not depend on real time. | `LOW` | `HEURISTIC` | `WARN` | `L:DirectSystemTimeUsageInTest` |
| **LAW-023** | Component lifecycle safety. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:LifecycleLeak` |
| **LAW-024** | Memory leak prevention (Context). | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:ContextLeak` |
| **LAW-026** | Efficient recomposition (Caching). | `MEDIUM` | `CERTAIN` | `WARN` | `L:ExpensiveRecomposition` |

### 🛡️ Security & Integrity
| Law ID | Law Description | Risk | Confidence | Enforcement | Enforcement Rule(s) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **LAW-010** | Zero-leak application logs. | `CRITICAL` | `HEURISTIC` | `WARN` | `L:SensitiveLogging`, `L:HardcodedSecrets` |
| **LAW-033** | Governance-compliant suppressions. | `CRITICAL` | `HIGH` | `BLOCK` | `L:SuppressionPolicyViolation`, `S:Law033_WildcardSuppressionTest`, `V:Law033_GradleSuppressionTest` |
| **LAW-034** | High-fidelity canary verification. | `CRITICAL` | `CERTAIN` | `BLOCK` | `L:LintCanaryActive`, `V:Law034_LintCanaryRegressionTest` |
| **LAW-035** | Structural non-baselining mandate. | `CRITICAL` | `CERTAIN` | `BLOCK` | `V:Law035_FatalBaselineIntegrityTest` |
| **LAW-040** | Baseline monotonicity mandate. | `HIGH` | `CERTAIN` | `BLOCK` | `V:BaselineMonotonicityTest` |

### ✨ Code Health & Infrastructure
| Law ID | Law Description | Risk | Confidence | Enforcement | Enforcement Rule(s) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **LAW-017** | Backing-property convention. | `LOW` | `HEURISTIC` | `INFO` | `L:BackingPropertyConvention` |
| **LAW-028** | Local method complexity budget. | `HIGH` | `CERTAIN` | `BLOCK` | `L:SpaghettiMethodFatal` |
| **LAW-029** | Global class size budget. | `HIGH` | `CERTAIN` | `BLOCK` | `L:GodObjectFatal` |
| **LAW-030** | Orchestration dependency budget. | `MEDIUM` | `CERTAIN` | `BLOCK` | `L:OrchestrationMonsterError`, `K:Law030_ConstructorPurityProcessor` |
| **LAW-037** | Version catalog mandate. | `CRITICAL` | `HIGH` | `BLOCK` | `T:checkDependencyDrift` |
| **LAW-038** | Release symbol obfuscation integrity. | `CRITICAL` | `CERTAIN` | `BLOCK` | `T:auditReleaseSymbols` |
| **LAW-039** | Magic literal extraction. | `LOW` | `HEURISTIC` | `INFO` | `L:MagicNumber` |

---

## ⚙️ The Monotonicity Ratchet (LAW-040)

To prevent architectural decay, Estatia enforces a **Strict Monotonicity Policy** for the `lint-baseline.xml` file.

1.  **Zero FATAL Policy**: Baseline entries for **BLOCK** level rules are forbidden. If a law is critical enough to block the build, it must be fixed, not hidden.
2.  **Directional Integrity**: The number of ERROR/WARN violations in the baseline can **never increase**. 
3.  **Mechanical Ratchet**: The CI pipeline compares the current baseline counts against the main branch. Any PR that increases the count of architectural debt will be blocked.

This ensures that technical debt is either strictly capped or actively reduced, preventing the "Baseline Inflation" that eventually makes static analysis meaningless.

---

## 🏗️ The CI Authority Model

The Estatia CI pipeline is the final word on engineering quality.

1.  **PR Gate**: Enforces all **BLOCK** level laws and verifies the **Lint Ratchet**.
2.  **Nightly run**: High-fidelity chaos, benchmark, and security stress tests.
3.  **Governance Sync**: Authoritative verification between Code, Docs, and Registry.
