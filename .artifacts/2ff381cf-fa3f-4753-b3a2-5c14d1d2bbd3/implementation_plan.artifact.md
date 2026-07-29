# Implementation Plan - Fix Package Name Mismatches

This plan addresses the incorrect package names and namespaces found in test classes and configuration files, which are currently using a placeholder `com.application.real_estate_app` instead of the project's actual namespace `com.estatia.realestate.apps`.

## User Review Required

> [!IMPORTANT]
> There is a distinction between `targetContext.packageName` and `context.packageName` in instrumented tests:
> - `InstrumentationRegistry.getInstrumentation().targetContext.packageName` returns the namespace of the module being tested (e.g., `com.estatia.realestate.apps.core.analytics`).
> - `InstrumentationRegistry.getInstrumentation().context.packageName` returns the package name of the test APK, which typically includes the `.test` suffix (e.g., `com.estatia.realestate.apps.core.analytics.test`).
>
> I will update the tests to use the correct project namespace. I will keep the use of `targetContext` and update the expected string to be the base namespace (without `.test`) as this is the standard way to verify the target application context.

## Proposed Changes

### Configuration Files

#### [MODIFY] [google-services.json](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/app/google-services.json)
- Update `package_name` from `com.application.real_estate_app` to `com.estatia.realestate.apps`.

### Feature Modules

#### [MODIFY] [feature:market](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/market/src/androidTest/java/com/estatia/realestate/apps/feature/market/MarketInstrumentedTest.kt)
- Fix incorrect package declaration at the top of the file.
- Update `assertEquals` in `useAppContext`.

#### [MODIFY] [feature:market](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/feature/market/src/test/java/com/estatia/realestate/apps/feature/market/MarketUnitTest.kt)
- Fix incorrect package declaration.

### All Modules (Instrumented Tests)

I will perform a bulk update on all `*InstrumentedTest.kt` files to fix the `assertEquals` in `useAppContext`.

#### Feature Modules
- `feature:chats` -> `com.estatia.realestate.apps.feature.chats`
- `feature:favorites` -> `com.estatia.realestate.apps.feature.favorites`
- `feature:intelligence` -> `com.estatia.realestate.apps.feature.intelligence`
- `feature:payments` -> `com.estatia.realestate.apps.feature.payments`
- `feature:service` -> `com.estatia.realestate.apps.feature.service`
- `feature:settings` -> `com.estatia.realestate.apps.feature.settings`

#### Core Modules
- `core:analytics` -> `com.estatia.realestate.apps.core.analytics`
- `core:common` -> `com.estatia.realestate.apps.core.common`
- `core:datastore` -> `com.estatia.realestate.apps.core.datastore`
- `core:design-system` -> `com.estatia.realestate.apps.core.designsystem`
- `core:domain` -> `com.estatia.realestate.apps.core.domain`
- `core:model` -> `com.estatia.realestate.apps.core.model`
- `core:network` -> `com.estatia.realestate.apps.core.network`
- `core:notifications` -> `com.estatia.realestate.apps.core.notifications`
- `core:security` -> `com.estatia.realestate.apps.core.security`
- `core:testing` -> `com.estatia.realestate.apps.core.testing`
- `core:ui` -> `com.estatia.realestate.apps.core.ui`

#### Others
- `lint` -> `com.estatia.realestate.apps.lint`

## Verification Plan

### Automated Tests
- Run all instrumented tests for the affected modules:
  `./gradlew connectedDemoDebugAndroidTest`
- Specifically check `useAppContext` in a few modules to ensure they pass.

### Manual Verification
- None required beyond test execution.
