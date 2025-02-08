
@file:Suppress("ConstPropertyName")
object HiltDeps {
    // Dagger Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.google_dagger_hilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.google_dagger_hilt}"
    const val hiltCompiler ="androidx.hilt:hilt-compiler:${Versions.androidx_hilt}"
    const val hiltNavigationFragment = "androidx.hilt:hilt-navigation-fragment:${Versions.androidx_hilt}"

    // Grouped Hilt Dependencies
    val AllHiltDependencies = listOf(
        hiltAndroid,
        hiltNavigationFragment
    )

    val AllHiltKaptDependencies = listOf(
        hiltCompiler,
        hiltAndroidCompiler
    )
}