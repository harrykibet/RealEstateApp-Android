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

## Usage
Apply these plugins in your `build.gradle.kts` files using the `alias` syntax from the version catalog:
```kotlin
plugins {
    alias(libs.plugins.estatia.android.feature)
    alias(libs.plugins.estatia.hilt)
}
```
