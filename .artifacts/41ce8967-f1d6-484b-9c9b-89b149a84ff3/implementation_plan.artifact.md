# Fix java.lang.OutOfMemoryError during Build

The project is experiencing a `java.lang.OutOfMemoryError: Java heap space` during the `:core:player-ui:mergeExtDexDemoDebugAndroidTest` task. This indicates that the Gradle daemon process does not have enough memory to complete the dexing and merging process for the large number of modules and dependencies in this project.

## User Review Required

> [!IMPORTANT]
> The proposed fix increases the maximum heap size for the Gradle daemon from 4GB to 8GB. Ensure that the machine running the build has at least 12-16GB of RAM to accommodate this change without causing system-wide performance issues.

## Proposed Changes

### Gradle Configuration

#### [MODIFY] [gradle.properties](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/gradle.properties)
- Increase `org.gradle.jvmargs` from `-Xmx4g` to `-Xmx8g` to provide more memory for the Gradle daemon.
- Add `android.enableDexingArtifactTransform.parallel=true` to potentially speed up dexing if multiple cores are available (optional but recommended for large projects).

## Verification Plan

### Manual Verification
- Run the failing command: `./gradlew :core:player-ui:mergeExtDexDemoDebugAndroidTest`
- Verify that the build completes successfully without `OutOfMemoryError`.
