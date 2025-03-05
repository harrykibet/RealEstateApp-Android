/**
 * **Plugins**
 *
 * This object contains constants representing the IDs of various Gradle plugins used in the project.
 * These constants can be used throughout the build scripts (e.g., in `build.gradle.kts` files)
 * to apply the plugins by their ID, ensuring consistency and avoiding string literal duplication.
 *
 * **Key Concepts:**
 * - **Gradle Plugins:** Extensions to the Gradle build system that add capabilities and features.
 * - **Plugin IDs:** Unique identifiers used to reference and apply specific plugins.
 * - **build.gradle.kts:** Kotlin-based Gradle build scripts.
 *
 * **Usage Example:**
 *
 * ```kotlin
 * plugins {
 *     id(Plugins.androidApplication)
 *     id(Plugins.kotlinAndroid)
 *     id(Plugins.hilt)
 *     id(Plugins.navigationSafeArgs)
 * }
 * ```
 *
 * **List of Plugins:**
 *
 * - **androidApplication:** The Android Application plugin for building Android applications.
 * - **androidLibrary:** The Android Library plugin for building Android library modules.
 * - **kotlinAndroid:** The Kotlin Android plugin for using Kotlin in Android projects.
 * - **googleServices:** The Google Services plugin for integrating with Google services (e.g., Firebase).
 * - **firebaseCrashlytics:** The Firebase Crashlytics plugin for crash reporting.
 * - **secretsPlugin:** The Google Maps Platform Secrets Gradle Plugin for managing API keys.
 * - **navigationSafeArgs:** The Safe Args plugin for passing data between navigation destinations.
 * - **kapt:** The Kotlin Annotation Processing Tool plugin.
 * - **hilt:** The Hilt plugin for dependency injection in Android projects.
 * - **dokka:** The Dokka plugin for generating Kotlin documentation.
 * - **sonarQube:** The SonarQube plugin for static code analysis.
 * - **room:** The Room plugin for using Room persistence library.
 * - **kotlinJvm:** The Kotlin JVM plugin for Kotlin/JVM projects
 * - **safeArgs:**  Alias for Navigation Safe Args plugin.
 */
@Suppress("constPropertyName", "MemberVisibilityCanBePrivate")
object Plugins {
    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroid = "org.jetbrains.kotlin.android"
    const val googleServices = "com.google.gms.google-services"
    const val firebaseCrashlytics = "com.google.firebase.crashlytics"
    const val secretsPlugin = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin"
    const val navigationSafeArgs = "androidx.navigation.safeargs.kotlin"
    const val kapt = "org.jetbrains.kotlin.kapt"
    const val hilt = "com.google.dagger.hilt.android"
    const val dokka = "org.jetbrains.dokka"
    const val sonarQube = "org.sonarqube"
    const val room = "androidx.room"
    const val kotlinJvm = "org.jetbrains.kotlin.jvm"
    const val safeArgs = "androidx.navigation.safeargs.kotlin"
}
