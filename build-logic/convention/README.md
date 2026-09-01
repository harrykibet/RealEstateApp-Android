# build-logic:convention

## Overview
This module contains the custom Gradle convention plugins used across the Estatia project to ensure consistent build configurations, enforce architecture boundaries, and reduce boilerplate in feature and core modules.

## Plugins
- **AndroidApplicationConventionPlugin**: Configures the main `:app` module with standard defaults (SDK versions, build types, etc.).
- **AndroidFeatureConventionPlugin**: Standardizes feature module configuration, including base dependencies (`:core:ui`, `:core:model`, etc.) and essential plugins.
- **AndroidCoreConventionPlugin**: Base configuration for core library modules.
- **ComposeConventionPlugin**: Enables and configures Jetpack Compose, including the compiler and base dependencies like Material 3 and Lifecycle Compose utilities.
- **HiltConventionPlugin**: Simplifies Dagger Hilt setup and KSP integration.
- **AndroidRoomConventionPlugin**: Configures Room database dependencies and compiler options.
- **TestingConventionPlugin**: Sets up JUnit 4, AndroidX Test, and shared testing utilities.
- **LintConventionPlugin**: Applies custom lint rules and project-wide lint configurations.
- **FirebaseConventionPlugin**: Handles Firebase plugin application and dependencies.
- **JacocoConventionPlugin**: Configures JaCoCo test coverage reporting and enforces quality gates with intelligent module-specific thresholds.

## Quality Gates & Coverage
The project uses JaCoCo to track test coverage. The `JacocoConventionPlugin` (aliased as `estatia.android.jacoco`) is automatically applied to app, core, and feature modules.

### Coverage Thresholds
Thresholds are enforced per module type to balance quality and velocity:
- **Mission Critical (90%)**: `:core:security`, `:core:player-engine`.
- **Infrastructure (80%)**: `:core:network`, `:core:database`, `:core:domain`.
- **Features (60%)**: Standard feature modules (e.g., `:feature:home`).
- **Data Models (30%)**: `:core:model`.

### Running Coverage
Reports are generated per variant (e.g., `ProdDebug` or `DemoDebug`):
```bash
./gradlew :core:security:jacocoProdDebugReport        # Generate HTML report
./gradlew :core:security:jacocoProdDebugVerification  # Run coverage gate check
```
HTML reports are generated at `[module]/build/reports/jacoco/jacoco[Variant]Report/html/index.html`.

## Usage
Apply these plugins in your `build.gradle.kts` files using the `alias` syntax from the version catalog:
```kotlin
plugins {
    alias(libs.plugins.estatia.android.feature)
    alias(libs.plugins.estatia.hilt)
}
```
