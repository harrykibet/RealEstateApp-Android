/**
 * `HiltDeps` is a utility object that centralizes and manages dependencies related to Dagger Hilt.
 * It provides easy access to Hilt libraries for different use cases, including core functionality,
 * compilation, and testing.
 *
 * This object offers predefined dependency instances and helper functions to conveniently
 * retrieve lists of dependencies for various configurations, such as:
 * - Regular Hilt dependencies for application modules.
 * - Annotation processor dependencies for code generation.
 * - Dependencies for Hilt-based testing.
 *
 * By using this object, you can streamline the management of Hilt dependencies within your project's
 * build configuration files (e.g., `build.gradle.kts`).
 */
@Suppress("MemberVisibilityCanBePrivate")
object HiltDeps {
    // Dagger Hilt dependencies
    val hiltAndroid = Dependency.VersionedDependency(
        group = "com.google.dagger",
        name = "hilt-android",
        version = Versions.daggerHilt
    )

    val hiltAndroidCompiler = Dependency.VersionedDependency(
        group = "com.google.dagger",
        name = "hilt-android-compiler",
        version = Versions.daggerHilt
    )

    val hiltAndroidTesting = Dependency.VersionedDependency(
        group = "com.google.dagger",
        name = "hilt-android-testing",
        version = Versions.daggerHilt
    )

    val hiltCompiler = Dependency.VersionedDependency(
        group = "androidx.hilt",
        name = "hilt-compiler",
        version = Versions.hiltAndroidx
    )

    val hiltNavigationFragment = Dependency.VersionedDependency(
        group = "androidx.hilt",
        name = "hilt-navigation-fragment",
        version = Versions.hiltAndroidx
    )

    // Functions to retrieve dependencies as lists
    fun getAllHiltDeps() = listOf(
        hiltAndroid,
        hiltNavigationFragment
    ).map { it.get() }

    fun getAllHiltKaptDeps() = listOf(
        hiltCompiler,
        hiltAndroidCompiler
    ).map { it.get() }

    fun getAllHiltTestingDeps() = listOf(
        hiltAndroidTesting
    ).map { it.get() }
}
