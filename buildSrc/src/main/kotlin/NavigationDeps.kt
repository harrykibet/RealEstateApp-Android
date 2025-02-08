

@file:Suppress("ConstPropertyName")
object NavigationDeps {
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.androidx_navigation}"
    const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.androidx_navigation}"

    val AllNavigationDependencies = listOf(
        navigationFragment,
        navigationUI
    )
}