
@Suppress("constPropertyName", "MemberVisibilityCanBePrivate")
object HiltDeps {
    // Dagger Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.daggerHilt}"
    const val hiltAndroidTesting = "com.google.dagger:hilt-android-testing:${Versions.daggerHilt}"
    const val hiltCompiler ="androidx.hilt:hilt-compiler:${Versions.hiltAndroidx}"
    const val hiltNavigationFragment = "androidx.hilt:hilt-navigation-fragment:${Versions.hiltAndroidx}"

    // Grouped Hilt Dependencies
    val AllHiltDeps = listOf(
        hiltAndroid,
        hiltNavigationFragment
    )

    val AllHiltKaptDeps = listOf(
        hiltCompiler,
        hiltAndroidCompiler
    )

    val AllHiltTestingDeps = listOf(
        hiltAndroidTesting
    )
}