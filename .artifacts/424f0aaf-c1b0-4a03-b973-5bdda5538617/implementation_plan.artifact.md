# Implementation Plan - Fix Suppressed Sync Exceptions (NPE in ModelCacheV2ImplKt)

The project is experiencing a `NullPointerException` during Gradle sync in Android Studio. The error `Parameter specified as non-null is null: ... toFlatLibraryList$6$7$0` usually indicates that a dependency in the Gradle model has an unexpected `null` value for a required field (like group, name, or version).

Based on research, several libraries in `libs.versions.toml` are defined using a shorthand string format or are missing explicit versions without being clearly managed by an applied BOM in all contexts. This can confuse the IDE's dependency flattener, especially with newer AGP versions.

## Proposed Changes

### [Component Name] Gradle Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/gradle/libs.versions.toml)
- Convert string-style library definitions to the more robust map-style (`group`, `name`, `version` or `module`, `version`).
- Add missing versions to libraries that are currently versionless and might not be correctly resolving from a BOM in the IDE's view.
- Specifically, address:
    - `gson`
    - `eventbus`
    - `caffeine`
    - `opentelemetry-api`
    - `opentelemetry-exporter-otlp`
    - `micrometer-core`
    - `micrometer-registry-prometheus`
    - `conscrypt-openjdk-uber`
    - `google-cloud-kms` (missing version)
    - `google-cloud-secretmanager` (missing version)
    - `androidx-compose-material3-adaptive` (and related adaptive libs)

#### [MODIFY] [ComposeConventionPlugin.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/build-logic/convention/src/main/kotlin/ComposeConventionPlugin.kt)
- Ensure all Compose-related dependencies that are explicitly added have their versions either in the catalog or correctly managed.

## Verification Plan

### Automated Tests
- Run `./gradlew help` to verify Gradle itself can parse the build files.
- Ideally, the user should trigger a Sync in Android Studio to verify the NPE is gone.

### Manual Verification
- Verify that `libs.versions.toml` is syntactically correct and all keys used in the project are still available.
