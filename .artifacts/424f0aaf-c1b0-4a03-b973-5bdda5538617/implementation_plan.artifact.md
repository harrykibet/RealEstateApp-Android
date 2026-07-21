# Implementation Plan - Standardize Logger Usages

The `ILogger` interface was updated to have `tag: String? = null` as its first parameter. This change requires updating all logger calls in the project to use named parameters (specifically `message = ...`) when the tag is omitted, or to correctly pass the tag if intended. Currently, many calls pass a single string which is being incorrectly assigned to the `tag` parameter.

## User Review Required

> [!IMPORTANT]
> This change affects multiple modules across the project to ensure consistent and correct logging behavior.

## Proposed Changes

### Core Modules

#### [MODIFY] [ApiKeyValidator.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/network/src/main/java/com/estatia/realestate/apps/core/network/utils/ApiKeyValidator.kt)
- Update `logger.d` to use `message = ...`.

#### [MODIFY] [SpatialAudioRenderer.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/player-engine/src/main/java/com/estatia/realestate/apps/core/player_engine/advanced/SpatialAudioRenderer.kt)
- Update `logger.i`, `logger.e`, and `logger.w` calls.
- Use `message = ...` for single-string calls.

#### [MODIFY] [PlaybackAnalyticsListener.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/player-engine/src/main/java/com/estatia/realestate/apps/core/player_engine/analytics/PlaybackAnalyticsListener.kt)
- Update `logger.e` calls within `analyticsClient.logEvent` callbacks to use `message = ...`.

#### [MODIFY] [PropertyViewModel.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/ui/src/main/java/com/estatia/realestate/apps/core/ui/viewmodels/PropertyViewModel.kt)
- Update the helper `log` function to use `message = ...` when calling `logger.e`.
- Alternatively, pass the class name as the `tag`.

#### [MODIFY] [AnalyticsTracker.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/data/src/main/java/com/estatia/realestate/apps/core/data/repositories/AnalyticsTracker.kt)
- Update `logger.e` calls to correctly map `message` and `throwable` using named parameters.

### Test Files

#### [MODIFY] [ApiKeyValidatorTest.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/network/src/test/java/com/estatia/realestate/apps/core/network/ApiKeyValidatorTest.kt)
- Update MockK `verify` blocks to match the new named parameter usage.

## Verification Plan

### Automated Tests
- Run `analyze_file` on all modified files to ensure no "No value passed for parameter 'message'" errors remain.
- Run unit tests for `ApiKeyValidator` to ensure the logic and logging verification still work.
    - Command: `./gradlew :core:network:testDebugUnitTest --tests "com.estatia.realestate.apps.core.network.ApiKeyValidatorTest"`

### Manual Verification
- Inspect the modified code to ensure `tag` and `message` are used as intended (usually `tag` is omitted or explicitly set to the component name).
