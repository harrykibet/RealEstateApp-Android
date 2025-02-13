
@Suppress("ConstPropertyName")
object TestingDeps {
    const val testExtJUnit = "androidx.test.ext:junit:${Versions.androidx_test_ext_junit}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.androidx_test_espresso_core}"
    const val junit = "junit:junit:${Versions.junit}"
    const val googleTruth = "com.google.truth:truth:${Versions.google_truth}"
    const val kotest = "io.kotest:kotest-runner-junit5:${Versions.kotest}"
    const val mock = "io.mockk:mockk:${Versions.mockk}"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android-instrumentation:${Versions.leakcanary}"

    //Grouped TestingDeps( AndroidTestImplementation(...) )
    val AndroidTestDependencies = listOf(
        testExtJUnit,
        espressoCore
    )
    // TestImplementation(...)
    val TestDependencies = listOf(
        junit,
        kotest,
        mock,
        leakCanary
    )
}