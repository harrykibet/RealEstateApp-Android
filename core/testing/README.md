# Estatia Test Platform (`core:testing`)

This module provides a unified, **adversarial test platform** for the Estatia application. It goes beyond simple mocking by centralizing reusable failure domains, deterministic synchronization, and expressive state assertions.

## 🚀 Key Architectural Pillars

### 1. Adversarial Chaos Injection
Instead of testing "happy paths," this platform provides tools to inject production-grade failures at logical boundaries.
- **`NetworkChaosController`**: Script complex connectivity sequences (e.g., `Timeout` -> `HttpError(503)` -> `Success`) to verify backoff and retry logic.
- **`ChaosFileSystem`**: Simulate `DiskFull`, `FileNotFound`, or `PermissionDenied` errors to test persistence resilience.
- **`ChaosStreamingPipeline`**: Inject segment-level failures in the media engine to test buffer stalls and watchdog recovery.
- **`ChaosEnvironmentController`**: Force extreme hardware states like **High Thermal Throttling** or **Low Battery** to verify adaptive UI scaling.

### 2. Deterministic Synchronization
Eliminate brittle `Thread.sleep()` and flaky timing with naming synchronization.
- **`TestClock`**: Fully manual virtual time control. Essential for testing debounce heuristics, TTL logic, and session timeouts.
- **`TestTicker`**: Micro-step simulation (e.g., 16ms ticks) for testing high-frequency UI updates and polling without real-world drift.
- **`TestScheduler`**: Named synchronization points (`awaitPoint` / `release`) to force deterministic interleaving in concurrent repository operations.
- **`runConcurrent`**: A robust utility for stress-testing race conditions by launching multiple operations in parallel with enforced synchronization.

### 3. Expressive Platform DSLs
Standardized assertions that provide high-fidelity failure diagnostics.
- **State DSL**: Use `viewModel.uiState.assertState { this is Success && data.isNotEmpty() }` for readable, contract-aware Flow verification.
- **Property DSL**: Use `viewModel.draft.assertProperty("Expected") { title }` for surgical verification of specific state fields.
- **Result DSL**: Use `result.assertSuccess()` or `result.assertError()` to cleanly unwrap `AppResult` types with built-in type inference.

### 4. High-Performance Fakes (`Witness` Pattern)
Standard MockK verification is slow and heavy. Our platform uses the **Witness Pattern** for high-frequency interactions.
- **`Witness<T>`**: An append-only log of events that can be verified later.
- **`FakeEngagementRepository`**: Records analytics signals without the overhead of `coVerify`.
- **`RecordingAnalyticsTracker`**: Standardized fake for verifying complex event telemetry sequences.

### 5. Standardized Fixtures
Reusable domain generators to ensure consistency across feature modules.
- **`PropertyFixtures`**: High-fidelity property models (Single, List, Paginated).
- **`UserGenerator`**: Deterministic user profile generation.
- **`AuthFixtures`**: Pre-configured authenticated user states.

---

## 🛠 Usage Guide

### Integration
The `core:testing` platform is automatically integrated into every module using the `com.estatia.realestate.apps.android.testing` plugin. 

If adding manually:
```kotlin
dependencies {
    testImplementation(testFixtures(projects.core.testing))
    androidTestImplementation(testFixtures(projects.core.testing))
}
```

### Example: Testing Resilience
```kotlin
@Test
fun `handles transient network failure`() = runTest {
    // 1. Script the failure
    chaosController.script(NetworkBehavior.Timeout, NetworkBehavior.Success)
    
    // 2. Perform operation
    viewModel.refresh()
    
    // 3. Verify state via DSL
    viewModel.uiState.assertState { this is Error && type == PlayerErrorType.NETWORK }
    
    // 4. Advance time and verify recovery
    testClock.advanceBy(retryDelay)
    viewModel.uiState.assertState { this is Success }
}
```

## 📁 Folder Structure

```text
src/testFixtures/kotlin/
├── assertions/    # assertState, assertSuccess, assertProperty
├── chaos/         # Network, FileSystem, and Streaming failure domains
├── clock/         # TestClock and TestTicker (Virtual Time)
├── coroutine/     # TestScheduler and runConcurrent (Synchronization)
├── fake/          # Witness-based fakes (Engagement, Analytics)
├── fixtures/      # Domain-specific data models
└── generators/    # Deterministic model factories
```
