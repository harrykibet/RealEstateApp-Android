
@Suppress("ConstPropertyName")
object TestingDeps {
    const val testExtJUnit = "androidx.test.ext:junit:${Versions.androidx_test_ext_junit}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.androidx_test_espresso_core}"
    const val junit = "junit:junit:${Versions.junit}"

    //Grouped TestingDeps( AndroidTestImplementation(...) )
    val AndroidTestDependencies = listOf(
        testExtJUnit,
        espressoCore
    )
    // TestImplementation(...)
    val TestDependencies = listOf(
        junit
    )
}