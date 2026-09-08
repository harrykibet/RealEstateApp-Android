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

### Tier 1: PR Check (High Maturity & Fast Feedback)
*   **Trigger**: Every Pull Request (GitHub/GitLab) or PR build (Codemagic).
*   **Goal**: Ensure architectural compliance and catch regressions in < 15 minutes.
*   **Key Operations**:
    *   **Level 0: Architecture Gate**: Runs `./gradlew verifyArchitecture` (Lint, KSP, Konsist, Canary violations, and Dependency Drift checks).
    *   **Level 1: Selective Verification**: Uses `./gradlew calculateImpact` to identify and run tests/lint only for impacted modules.
    *   **Level 2: Coverage Ratchet**: Verifies code coverage hasn't regressed using `./gradlew jacocoProdDebugVerification`.
    *   **Level 3: Smoke UI Suite**: Instrumented tests on a single `pixel2Api34` device to verify basic app startup.
    *   **Self-Healing**: Automatically updates lint baselines if violations are resolved (Ratchet mechanism).

### Tier 2: Main Branch (Full Verification)
*   **Trigger**: Merge/Push to `main`.
*   **Goal**: Ensure release readiness and multi-device compatibility.
*   **Key Operations**:
    *   **Exhaustive Analysis**: Full `./gradlew lint` and `./gradlew test` across all modules.
    *   **Fleet Testing**: Runs instrumented tests across four distinct device shapes (Small Phone, Modern Pixel, Medium Phone, Tablet).
    *   **Release Build**: `assembleProdRelease` with full R8 shrinking and production signing.
    *   **Artifact Archival**: Signed APKs are archived for internal QA distribution.

### Tier 3: Nightly (Stress & Optimization)
*   **Trigger**: Scheduled daily at 00:00 UTC.
*   **Goal**: Heavy-duty performance tuning, security auditing, and chaos validation.
*   **Key Operations**:
    *   **Deterministic Chaos**: Runs `FeedGestureChaosTest` on `pixel6Api31` to find rare race conditions.
    *   **Baseline Profile Generation**: Captures AOT profiles and automatically creates a PR to update them in the repo.
    *   **Performance Benchmarks**: Measures Startup and Scrolling performance via Macrobenchmarks.
    *   **Security & Purity**: Runs OWASP Dependency Check and `auditBinaryPurity` (R8 mapping audit).
    *   **Full Quality Audit**: Generates exhaustive JaCoCo coverage reports and runs architecture audits on all variants.

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
# Run the Architecture Gate
./gradlew verifyArchitecture

# Run the PR Smoke Suite
./gradlew :app:pixel2Api34ProdDebugAndroidTest

# Run the Full Fleet Test (Main Tier)
./gradlew :app:allDevicesProdDebugAndroidTest

# Generate Baseline Profiles (Nightly Tier)
./gradlew :app:generateBaselineProfile

# Run Performance Benchmarks
./gradlew :benchmark:pixel6Api31ProdDebugAndroidTest

# Run Binary Purity Audit
./gradlew auditBinaryPurity
```

---

## 🔒 Security & Secrets
CI platforms require the following secrets to be configured:
- `KEYSTORE_BASE64`: The production signing key encoded in Base64.
- `KEYSTORE_PASSWORD` / `KEY_ALIAS` / `KEY_PASSWORD`: Signing credentials.
- `SONAR_TOKEN`: Token for SonarQube analysis (used in quality stages).
- `FIREBASE_TOKEN`: Token for deploying Firestore/Storage rules.
- `GITHUB_TOKEN` / `CI_PUSH_TOKEN`: To allow committing Baseline Profiles or Lint updates back to the repo.
