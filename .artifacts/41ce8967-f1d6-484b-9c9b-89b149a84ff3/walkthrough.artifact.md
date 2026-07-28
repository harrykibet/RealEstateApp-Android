# Walkthrough - Fix StartupBenchmark IDE Run Configuration (Part 2)

I have fully aligned the `:benchmark` module with the `:app` module by adding flavor support and dynamic package name detection. This ensures that the IDE can correctly link the benchmark variants with the app variants, resolving the "Cannot obtain the package" error.

## Changes Made

### Build Logic & Flavors Alignment

#### [ConfigureFlavors.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/build-logic/convention/src/main/kotlin/com/estatia/realestate/apps/ConfigureFlavors.kt)
- Added an overload for `TestExtension` to support flavor configuration in `com.android.test` modules (like our benchmark module).

#### [AndroidFlavorsConventionPlugin.kt](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/build-logic/convention/src/main/kotlin/AndroidFlavorsConventionPlugin.kt)
- Updated the plugin to automatically apply flavors when the `com.android.test` plugin is present.

#### [benchmark/build.gradle.kts](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/benchmark/build.gradle.kts)
- Applied the `estatia.android.flavors` plugin.
- Removed the manual `missingDimensionStrategy`, as flavors are now natively supported.

### Dynamic Package Detection

#### Benchmark Files
- Updated `StartupBenchmark.kt`, `ScrollBenchmark.kt`, and `BaselineProfileGenerator.kt` to use `InstrumentationRegistry.getInstrumentation().targetContext.packageName`.
- This ensures the benchmarks always target the correct package name, whether it's the base package or one with a flavor suffix (e.g., `.demo`).

## Verification Results

### Project Sync
- Ran a full Gradle sync.
- Result: **Sync finished successfully.**

> [!IMPORTANT]
> **Action Required**:
> 1. Open the **Build Variants** tool window.
> 2. You will now see `demoBenchmark` and `prodBenchmark` for the `:benchmark` module.
> 3. **Match them**: Set `:app` to `demoBenchmark` and `:benchmark` to `demoBenchmark`.
> 4. You should now be able to run the benchmarks directly from the IDE gutter.
