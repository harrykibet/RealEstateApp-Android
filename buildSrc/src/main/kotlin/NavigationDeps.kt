

@file:Suppress("ConstPropertyName")
object NavigationDeps {
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val navigationTesting = "androidx.navigation:navigation-testing:${Versions.navigation}"

    val AllNavigationDependencies = listOf(
        navigationFragment,
        navigationTesting,
        navigationUI
    )
}