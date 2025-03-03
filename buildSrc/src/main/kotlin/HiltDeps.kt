
@file:Suppress("ConstPropertyName")
object HiltDeps {
    // Dagger Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.daggerHilt}"
    const val hiltCompiler ="androidx.hilt:hilt-compiler:${Versions.hiltAndroidx}"
    const val hiltNavigationFragment = "androidx.hilt:hilt-navigation-fragment:${Versions.hiltAndroidx}"

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