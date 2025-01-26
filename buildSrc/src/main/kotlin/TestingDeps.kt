
@Suppress("ConstPropertyName")
object TestingDeps {
    const val testExtJUnit = "androidx.test.ext:junit:${Versions.testExtJUnit}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    const val junit = "junit:junit:${Versions.junit}"

    //Grouped TestingDeps
    val androidTestDependencies = listOf(
        testExtJUnit,
        espressoCore
    )
    val TestDependencies = listOf(
        junit
    )
}