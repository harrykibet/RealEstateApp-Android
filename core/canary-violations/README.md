# Estatia Architectural Canary & Specification System (`:core:canary-violations`)

This module serves as the **Living Architectural Specification** for the Estatia codebase. It functions as both a "Ground Truth" for enforcement verification and an educational hub for developers.

---

## 🎯 The Hybrid Mission

### 1. High-Fidelity Verification (Ground Truth)
While unit tests in the `:lint` module verify detectors in isolation, this module verifies them in a **real multi-module environment**. It ensures that:
- **Resolution Fidelity**: Detectors correctly resolve types across module boundaries.
- **Crash Resilience**: Detectors are robust against complex classpaths and AGP updates.
- **Enforcement Integrity**: Rules are actually running in the CI pipeline (Heartbeat check).

### 2. Living Documentation (The Spec)
Every architectural law is implemented here as a **Modular Specification**. Developers can open these files to see:
- **Failing Code**: Intentional violations with precise rationale.
- **Passing Code**: Best-practice implementations of the same patterns.
- **Boundary Cases**: Edge cases that test the limits of the enforcement logic.

---

## 🏗️ Modular Specification Structure

Laws are organized into logical groups within the `specs/` directory. Each spec file follows a consistent pattern of **Positive**, **Negative**, and **Adversarial** examples.

### Tag System (The Enforcement Oracle)
The [High-Fidelity Oracle](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/lint/src/test/kotlin/com/estatia/realestate/apps/lint/policy/Law034_LintCanaryRegressionTest.kt) uses specific tags in the source code to verify enforcement behavior:

| Tag | Purpose | Build Outcome |
| :--- | :--- | :--- |
| `[CANARY:POSITIVE:IssueId]` | Mark an intentional violation. | **FAIL** if NOT detected on this line. |
| `[CANARY:NEGATIVE:IssueId]` | Mark a valid pattern that looks similar to a violation. | **FAIL** if incorrectly detected (False Positive). |

---

## ⚖️ Specification Catalog

| Specification File | Laws Covered | Enforcement Areas |
| :--- | :--- | :--- |
| [`Law008_Law009_ApiDesignSpec.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/canary-violations/src/main/kotlin/com/estatia/realestate/apps/core/canary_violations/specs/Law008_Law009_ApiDesignSpec.kt) | LAW-008, LAW-009 | Public Abstractions, Visibility, Result Wrappers. |
| [`Law001_002_026_027_ComposeSpec.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/canary-violations/src/main/kotlin/com/estatia/realestate/apps/core/canary_violations/specs/Law001_Law002_Law026_Law027_ComposeSpec.kt) | LAW-001, 002, 026, 027 | Logic in UI, State Ownership, Recomposition Jank. |
| [`Law012_Law018_Law023_Law029_StateSpec.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/canary-violations/src/main/kotlin/com/estatia/realestate/apps/core/canary_violations/specs/Law012_Law018_Law023_Law029_StateSpec.kt) | LAW-012, 018, 023, 029 | SSoT ViewModels, Thread Safety, God Objects. |
| [`Law019_006_013_020_021_ConcurrencySpec.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/canary-violations/src/main/kotlin/com/estatia/realestate/apps/core/canary_violations/specs/Law019_Law006_Law013_Law020_Law021_ConcurrencySpec.kt) | LAW-019, 006, 013, 020, 021 | Structured Concurrency, Hardcoded Dispatchers. |
| [`Law010_SecuritySpec.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/canary-violations/src/main/kotlin/com/estatia/realestate/apps/core/canary_violations/specs/Law010_SecuritySpec.kt) | LAW-010 | Hardcoded Secrets, Sensitive PII Logging. |
| [`Law007_Law015_Law016_EnvironmentSpec.kt`](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/canary-violations/src/main/kotlin/com/estatia/realestate/apps/core/canary_violations/specs/Law007_Law015_Law016_EnvironmentSpec.kt) | LAW-007, 015, 016 | System Time, Mock leakage in Production. |

---

## 🛡️ Governance Integration (LAW-034)

The enforcement integrity is maintained by `Law034_LintCanaryRegressionTest`. This test is part of the `verifyArchitecture` task and performs the following:
1.  **Tag Discovery**: Scans all `.kt` files in `src/main` and `src/test` for `[CANARY:...]` markers.
2.  **Report Analysis**: Parses the generated `lint-results.xml` from this module.
3.  **Oracle Verification**: Matches tags against actual violations using a High-Fidelity proximity model (+/- 10 lines).
4.  **Parity Enforcement**: Ensures that **every registered Lint rule** in the codebase has at least one specification defined in this module.

---

## ⚙️ Adding a New Specification

When implementing a new Architectural Law or Detector:
1.  **Define the Law** in the [`Law` registry](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/architecture/src/main/kotlin/com/estatia/realestate/apps/core/architecture/Law.kt).
2.  **Create/Update a Spec File** in `specs/`.
3.  **Add a Positive Canary**:
    ```kotlin
    // [CANARY:POSITIVE:NewRuleId]
    val violatingCode = ...
    ```
4.  **Add a Negative Canary**:
    ```kotlin
    // [CANARY:NEGATIVE:NewRuleId]
    val correctCode = ...
    ```
5.  **Verify**: Run `./gradlew :lint:test` to ensure the oracle sees your new specification.

## ⚠️ Warning

**THIS MODULE IS INTENDED TO FAIL.** 

`abortOnError` is disabled in `build.gradle.kts` to allow the build to proceed and generate the report that the Oracle needs. Do not attempt to fix the errors reported in this module; instead, use them as a reference for how to write compliant code elsewhere.
