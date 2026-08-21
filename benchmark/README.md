# benchmark

## Overview
The `benchmark` module is responsible for performance measurement and optimization of the Estatia application. It uses the AndroidX Macrobenchmark library to track critical user journeys and generate performance profiles.

## Responsibilities
- **Startup Benchmarks**: Measures the time from app launch to the first frame being drawn.
- **Scroll Benchmarks**: Measures frame drops and stability during vertical feed scrolling.
- **Baseline Profile Generation**: Generates profiles that are bundled with the app to improve cold start performance and reduce "jank" on first use.

## Key Files
- `StartupBenchmark.kt`: Automates cold, warm, and hot startup tests.
- `ScrollBenchmark.kt`: Simulates user scrolling in the property feed.
- `BaselineProfileGenerator.kt`: Triggers critical paths to record necessary classes and methods for AOT compilation.

## How to Run
Run the benchmarks using a physical device (ideally unrooted and non-emulator for accuracy):
```bash
./gradlew :benchmark:connectedDemoDebugAndroidTest
```
To generate baseline profiles:
```bash
./gradlew :app:generateDemoDebugBaselineProfile
```
