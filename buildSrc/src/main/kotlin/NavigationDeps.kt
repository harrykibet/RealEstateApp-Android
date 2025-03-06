/**
 * Object containing dependencies related to Android Jetpack Navigation.
 *
 * This object provides convenient access to the Navigation Fragment, Navigation UI,
 * and Navigation Testing dependencies. It also includes a helper function to retrieve
 * all these dependencies in a single list.
 */
@Suppress("MemberVisibilityCanBePrivate")
object NavigationDeps {
    val navigationFragment = Dependency.VersionedDependency(
        group = "androidx.navigation",
        name = "navigation-fragment-ktx",
        version = Versions.navigation
    ).toGradleNotation

    val navigationUI = Dependency.VersionedDependency(
        group = "androidx.navigation",
        name = "navigation-ui-ktx",
        version = Versions.navigation
    ).toGradleNotation

    val navigationTesting = Dependency.VersionedDependency(
        group = "androidx.navigation",
        name = "navigation-testing",
        version = Versions.navigation
    ).toGradleNotation

    // Function to Retrieve All Dependencies
    fun getAllNavigationDeps() = listOf(
        navigationFragment,
        navigationTesting,
        navigationUI
    )
}
