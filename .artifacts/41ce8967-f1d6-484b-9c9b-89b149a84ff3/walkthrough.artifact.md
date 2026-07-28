# Walkthrough - Fix Build OutOfMemoryError

I have increased the memory allocation for the Gradle daemon to resolve the `java.lang.OutOfMemoryError` encountered during the dex merging process.

## Changes Made

### Gradle Configuration

#### [gradle.properties](file:///C:/Users/Administrator/StudioProjects/RealEstateApp-Android/gradle.properties)

- Increased `org.gradle.jvmargs` maximum heap size (`-Xmx`) from `4g` to `8g`.
- Enabled `android.enableDexingArtifactTransform.parallel=true` to optimize the dexing process.

```diff
-org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8 -XX:+UseG1GC
+org.gradle.jvmargs=-Xmx8g -Dfile.encoding=UTF-8 -XX:+UseG1GC
+android.enableDexingArtifactTransform.parallel=true
```

## Verification Results

### Automated Tests
- Ran the specific task that was failing:
  ```bash
  ./gradlew :core:player-ui:mergeExtDexDemoDebugAndroidTest
  ```
- Result: **Build finished successfully.**
