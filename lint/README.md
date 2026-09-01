# Estatia Quality Engine (`:lint`)

This module houses the custom Android Lint suite that enforces the **Estatia Production-Grade Class Standard**. It transforms our engineering documentation into automated build-time safety gates.

## 🏗️ Core Philosophy
In Estatia, we don't just "trust" developers to follow architectural patterns—we verify them with the compiler. Every custom rule in this module is set to `Severity.ERROR`. 

> [!IMPORTANT]
> **A violation of any rule below will fail the build.** This ensures that our high engineering bar is maintained automatically as the team scales.

---

## 🛡️ Enforced Standards (The 20 Gates)

### 1. State Ownership & Invariants
| Rule ID | Description | How to Fix |
| :--- | :--- | :--- |
| `ExposedMutableState` | Prevents exposing `MutableStateFlow` or mutable collections publicly. | Expose as `StateFlow` or `List` instead. Use `_state` for private mutation. |
| `ModulePackageMismatch` | Ensures class packages match their module folder structure. | Move the file to the correct package path defined in documentation. |
| `NonThreadSafeCollection` | Blocks unsafe collections in Singletons without synchronization. | Use `ConcurrentHashMap` or protect access with a `Mutex`. |

### 2. Structured Concurrency & Lifecycle
| Rule ID | Description | How to Fix |
| :--- | :--- | :--- |
| `SwallowedCancellationException` | Prevents broad catch blocks from swallowing coroutine cancellation. | Add `catch (e: CancellationException) { throw e }` before your broad catch. |
| `ForbiddenCoroutineScope`| Blocks `GlobalScope` or unmanaged `CoroutineScope` in ViewModels/Repos. | Use `viewModelScope` or inject a managed scope via Hilt. |
| `UnsafeStateCollection` | Prevents using `collectAsState()` in Compose. | Use `collectAsStateWithLifecycle()` to prevent background resource leaks. |
| `RememberMissing` | Flags state creation in Compose without `remember`. | Wrap `mutableStateOf` or `mutableListOf` in a `remember { ... }` block. |

### 3. Dependency Inversion & Testability
| Rule ID | Description | How to Fix |
| :--- | :--- | :--- |
| `InfrastructureLeakage` | Blocks SDK types (Firebase/AWS) in domain interfaces. | Map SDK types to domain models or `AppResult` at the data layer boundary. |
| `ServiceLocatorUsage` | Blocks static `getInstance()` calls (e.g., `FirebaseAuth.getInstance()`). | Provide the dependency via a Hilt module and use constructor injection. |
| `HardcodedDispatcher` | Blocks direct usage of `Dispatchers.IO` or `Dispatchers.Main`. | Inject a `CoroutineDispatcher` (e.g., using `@IODispatcher` qualifier). |
| `DirectSystemTimeAccess` | Blocks `System.currentTimeMillis()`. | Inject a `Clock` or `Instant` provider for deterministic testing. |

### 4. Security & Performance
| Rule ID | Description | How to Fix |
| :--- | :--- | :--- |
| `SensitiveLogging` | Prevents logging of tokens, passwords, or PII. | Sanitize logs. Use identifiers or hashes instead of raw sensitive data. |
| `UnboundedInternalBuffer`| Blocks `Channel.UNLIMITED` or massive buffer sizes. | Use a bounded capacity (e.g., `Channel(64)`) to handle backpressure safely. |
| `MissingResultWrapper` | Mandates `AppResult` for all repository suspend functions. | Wrap return types in `AppResult<T>` to enforce explicit failure handling. |
| `MissingConcurrencyCheck`| Ensures critical infrastructure enforces thread confinement. | Add `checkConfinement()` at the start of public infrastructure methods. |

### 5. Global Scalability & Design
| Rule ID | Description | How to Fix |
| :--- | :--- | :--- |
| `HardcodedString` | Blocks string literals in UI components. | Move the text to `strings.xml` and use `stringResource(R.string.id)`. |
| `HardcodedColorDimension` | Blocks Hex colors (`#FFF`) and DP values (`16.dp`) in UI. | Use `MaterialTheme.colorScheme` or `DesignSystem` tokens. |
| `DesignSystemUsage` | Prevents raw Material 3 usage outside the Design System module. | Use `EstatiaText`, `EstatiaButton`, etc., from `:core:design-system`. |
| `BusinessLogicInCompose` | Blocks complex logic/repository calls inside `@Composable`. | Move logic to the ViewModel and expose result via state. |

### 6. Test Integrity
| Rule ID | Description | How to Fix |
| :--- | :--- | :--- |
| `MockInProduction` | Blocks MockK/Mockito imports in production source sets. | Ensure mocking libraries are only present in `src/test` or `src/androidTest`. |

---

## 🛠️ Usage for Developers

### Running Locally
To verify your changes before pushing:
```bash
# Run lint for the entire project
./gradlew lint

# Run lint for a specific module
./gradlew :feature:home:lint
```

### Viewing Reports
Detailed HTML reports are generated at:
`[module-root]/build/reports/lint-results.html`

### Suppressing Rules (The "Break-Glass" Procedure)
If a specific case justifies bypassing a rule (rare), use the `@SuppressLint` annotation with the specific Rule ID:
```kotlin
@SuppressLint("ExposedMutableState")
val legacyPublicState = MutableStateFlow(...) // Document WHY this is needed
```

---

## 🧪 Development
To add a new rule to the Estatia standard:
1. Create a new `Detector` in `src/main/java`.
2. Register it in `EstatiaIssueRegistry`.
3. **Mandatory**: Add unit tests in `src/test/java` using `LintDetectorTest` to verify both happy and failure paths.
