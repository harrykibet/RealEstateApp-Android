# Estatia CI/CD Strategy

This document outlines the tiered CI/CD architecture for the Estatia Android project. Our pipeline is designed based on **Google's Modern Android Development (MAD)** best practices to balance developer velocity, hardware validation, and production-grade stability.

---

## 🏗️ The Multi-Platform Foundation

Estatia maintains identical CI/CD logic across three platforms to prevent vendor lock-in and ensure consistency for all contributors:
- **GitHub Actions**: Primary automation for PRs and Releases.
- **GitLab CI**: Secondary runner support for enterprise mirroring.
- **Codemagic**: Dedicated mobile CI for high-performance builds and real-device workflows.

All platforms utilize **Gradle Managed Devices (GMD)** to ensure that tests run on identical virtual hardware environments, eliminating "it works on my machine" issues.

---

## 🚦 The Three-Tiered Pipeline

We use a "funnel" approach where expensive operations are deferred to later stages.

### Tier 1: PR Check (Fast Feedback)
*   **Trigger**: Every Pull Request (GitHub/GitLab) or PR build (Codemagic).
*   **Goal**: Catch obvious regressions in < 10 minutes.
*   **Key Operations**:
    *   `./gradlew lint`: Static analysis to catch code smells.
    *   `./gradlew test`: All Unit tests (JVM).
    *   **Smoke UI Suite**: Instrumented tests on a single `pixel2Api34` device to verify basic app startup and login.

### Tier 2: Main Branch (Full Verification)
*   **Trigger**: Merge/Push to `main`.
*   **Goal**: Ensure release readiness and multi-device compatibility.
*   **Key Operations**:
    *   **Fleet Testing**: Runs instrumented tests across four distinct device shapes (Small Phone, Modern Pixel, Medium Phone, Tablet).
    *   **Release Build**: `assembleProdRelease` to verify R8/Proguard shrinking and resource optimization.
    *   **Artifact Archival**: Uploads the signed APK for internal QA distribution.

### Tier 3: Nightly (Stress & Optimization)
*   **Trigger**: Scheduled daily at 00:00 UTC.
*   **Goal**: Heavy-duty performance tuning and chaos validation.
*   **Key Operations**:
    *   **Deterministic Chaos**: Runs `FeedGestureChaosTest` with fixed seeds to find rare race conditions.
    *   **Baseline Profile Generation**: Automatically captures and commits AOT compilation profiles to keep the app smooth.
    *   **Macrobenchmarks**: Measures Startup and Scrolling performance; results are archived for historical comparison.

---

## 📱 Supported Hardware Fleet (GMD)

The project defines a standardized fleet in `build-logic` used by all CI runners:

| Device Name | Type | API Level | Purpose |
| :--- | :--- | :--- | :--- |
| `pixel2Api34` | Small Phone | 34 | Speed / Legacy Layouts |
| `pixel6Api31` | Modern Phone | 31 | Performance / Android 12+ |
| `mediumPhoneApi33` | Standard | 33 | Common Density Validation |
| `pixelTabletApi34` | Tablet | 34 | Large Screen / Adaptive UI |

---

## 🛠️ Operational Commands

Developers can run any CI job locally using these commands:

```bash
# Run the PR Smoke Suite
./gradlew :app:pixel2Api34ProdDebugAndroidTest

# Run the Full Fleet Test (Main Tier)
./gradlew :app:allDevicesProdDebugAndroidTest

# Generate Baseline Profiles (Nightly Tier)
./gradlew :app:generateProdBaselineProfile

# Run Performance Benchmarks
./gradlew :benchmark:pixel6Api31ProdDebugAndroidTest
```

---

## 🔒 Security & Secrets
CI platforms require the following secrets to be configured in their respective environment settings:
- `KEYSTORE_BASE64`: The production signing key encoded in Base64.
- `KEYSTORE_PASSWORD` / `KEY_ALIAS` / `KEY_PASSWORD`: Signing credentials.
- `SONAR_TOKEN`: Token for SonarQube analysis.
- `FIREBASE_TOKEN`: Token for deploying Firestore/Storage rules.
- `CI_PUSH_TOKEN` (GitLab only): To allow committing Baseline Profiles back to the repo.
