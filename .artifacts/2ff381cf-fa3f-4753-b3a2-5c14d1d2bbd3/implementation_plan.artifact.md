# Implementation Plan - Resolve Lint Errors and Optimize Configuration

This plan addresses the current lint errors that are blocking the build and optimizes the lint configuration to prevent unfixable warnings from 3rd-party dependencies.

## User Review Required

> [!IMPORTANT]
> I am disabling `checkDependencies` in the `LintConventionPlugin`. This prevents Lint from scanning transitive 3rd-party JARs (like BouncyCastle), which currently produces security warnings we cannot fix. This is a common practice in large projects to reduce noise and build times.

## Proposed Changes

### [Build Logic]

#### [MODIFY] [LintConventionPlugin.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/build-logic/convention/src/main/kotlin/LintConventionPlugin.kt)
- Change `checkDependencies = true` to `checkDependencies = false` to avoid scanning external JARs for lint violations.

### [Core: Common]

#### [MODIFY] [FileUtils.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/common/src/main/java/com/estatia/realestate/apps/core/common/system/FileUtils.kt)
- Replace `Uri.parse(...)` with the KTX extension `.toUri()` to satisfy the `UseKtx` check.

### [Core: Notifications]

#### [MODIFY] [strings.xml](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/notifications/src/main/res/values/strings.xml)
- Convert `core_notifications_properties_notification_group_summary` from a `<string>` to a `<plurals>` resource to resolve the `PluralsCandidate` warning.

### [Core: Design System]

#### [NEW] [google.png](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/design-system/src/main/res/drawable-nodpi/google.png)
#### [DELETE] [google.png](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/design-system/src/main/res/drawable/google.png)
- Move the density-independent bitmap from `drawable/` to `drawable-nodpi/` to satisfy the `IconLocation` check.

### [App Module]

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/app/src/main/AndroidManifest.xml)
- Add `tools:targetApi="33"` to the `application` tag to suppress the `UnusedAttribute` warning for `enableOnBackInvokedCallback`.

## Verification Plan

### Automated Tests
- Run `./gradlew lintDemoDebug` on the root project.
- **Success Criteria**: The build should finish successfully with 0 errors.

### Manual Verification
- Verify that notifications still display correctly with the new plurals resource.
- Verify that the Google sign-in button still shows its icon.
