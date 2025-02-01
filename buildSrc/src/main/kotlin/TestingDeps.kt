
@Suppress("ConstPropertyName")
object TestingDeps {
    const val testExtJUnit = "androidx.test.ext:junit:${Versions.androidx_test_ExtJUnit}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.androidx_test_espressoCore}"
    const val junit = "junit:junit:${Versions.junit}"

    //Grouped TestingDeps( AndroidTestImplementation(...) )
    val androidTestDependencies = listOf(
        testExtJUnit,
        espressoCore
    )
    // TestImplementation(...)
    val TestDependencies = listOf(
        junit
    )
}