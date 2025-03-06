/**
 * `CoreDeps` is a collection of core Android dependencies, providing easy access to common libraries
 * used in Android projects. It encapsulates versioned dependencies from various AndroidX and
 * Google libraries.
 *
 * This object centralizes the management of these dependencies, reducing redundancy and ensuring
 * consistency across modules within a project.
 *
 * It includes commonly used components such as:
 * - Core KTX: Kotlin extensions for the Android core libraries.
 * - AppCompat: Compatibility library for supporting older Android versions.
 * - Material Design: Implementation of Google's Material Design system.
 * - Constraint Layout: A flexible layout system for complex UI design.
 * - RecyclerView: Efficient display of large datasets in a list or grid.
 * - ViewPager2: A component for navigating between pages of content.
 * - Fragment KTX: Kotlin extensions for Fragments.
 * - Activity KTX: Kotlin extensions for Activities.
 * - Swipe Refresh Layout: A container that provides the swipe-to-refresh gesture.
 * - Splash Screen: API for implementing app launch animation.
 * - Work Manager: Library for background task scheduling.
 * - Test Core Ktx : Kotlin extensions for the testing core libraries.
 *
 * The object also provides utility functions to retrieve lists of frequently grouped dependencies:
 * - [getCommonCoreDeps]: Returns a list of the most commonly used core dependencies.
 * - [getCoreUiDeps]: Returns a list of core dependencies related to UI components.
 *
 * This improves the maintainability and readability of build scripts, ensuring that the correct
 * versions of dependencies are used throughout the project.
 */
@Suppress("MemberVisibilityCanBePrivate")
object CoreDeps {
    val coreKtx = Dependency.VersionedDependency(
        group = "androidx.core",
        name = "core-ktx",
        version = Versions.coreKtx
    ).toGradleNotation

    val appCompat = Dependency.VersionedDependency(
        group = "androidx.appcompat",
        name = "appcompat",
        version = Versions.appCompat
    ).toGradleNotation

    val material = Dependency.VersionedDependency(
        group = "com.google.android.material",
        name = "material",
        version = Versions.material
    ).toGradleNotation

    val constraintLayout = Dependency.VersionedDependency(
        group = "androidx.constraintlayout",
        name = "constraintlayout",
        version = Versions.constraintLayout
    ).toGradleNotation

    val viewPager2 = Dependency.VersionedDependency(
        group = "androidx.viewpager2",
        name = "viewpager2",
        version = Versions.viewPager
    ).toGradleNotation

    val recyclerView = Dependency.VersionedDependency(
        group = "androidx.recyclerview",
        name = "recyclerview",
        version = Versions.recyclerView
    ).toGradleNotation

    val swipeRefreshLayout = Dependency.VersionedDependency(
        group = "androidx.swiperefreshlayout",
        name = "swiperefreshlayout",
        version = Versions.swipeRefreshLayout
    ).toGradleNotation

    val fragmentKtx = Dependency.VersionedDependency(
        group = "androidx.fragment",
        name = "fragment-ktx",
        version = Versions.fragmentKtx
    ).toGradleNotation

    val activityKtx = Dependency.VersionedDependency(
        group = "androidx.activity",
        name = "activity-ktx",
        version = Versions.activityKtx
    ).toGradleNotation

    val splashScreen = Dependency.VersionedDependency(
        group = "androidx.core",
        name = "core-splashscreen",
        version = Versions.splashScreen
    ).toGradleNotation

    val testCoreKtx = Dependency.VersionedDependency(
        group = "androidx.test",
        name = "core-ktx",
        version = Versions.testCoreKtx
    ).toGradleNotation

    val workRuntimeKtx = Dependency.VersionedDependency(
        group = "androidx.work",
        name = "work-runtime-ktx",
        version = Versions.workManager
    ).toGradleNotation

    fun getCommonCoreDeps() = listOf(
        coreKtx,
        appCompat,
        material,
        fragmentKtx,
        activityKtx
    )

    fun getCoreUiDeps() = listOf(
        material,
        constraintLayout,
        recyclerView,
        viewPager2,
        swipeRefreshLayout
    )
}
