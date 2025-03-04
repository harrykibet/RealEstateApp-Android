@Suppress("ConstPropertyName")
object TestingDeps {
    const val testExtJUnit = "androidx.test.ext:junit:${Versions.testExtJunit}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    const val espressoIntents = "androidx.test.espresso:espresso-intents:${Versions.espressoIntents}"
    const val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espressoContrib}"
    const val junit = "junit:junit:${Versions.junit}"
    const val coreTesting = "androidx.arch.core:core-testing:${Versions.coreTesting}"
    const val junitJupiter = "org.junit.jupiter:junit-jupiter:${Versions.jupiter}"
    const val googleTruth = "com.google.truth:truth:${Versions.truth}"
    const val kotest = "io.kotest:kotest-runner-junit5:${Versions.kotest}"
    const val mock = "io.mockk:mockk:${Versions.mockk}"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android-instrumentation:${Versions.leakCanary}"
    const val uiautomator = "androidx.test.uiautomator:uiautomator:${Versions.uiAutomator}"


    //Grouped TestingDeps (AndroidTestImplementation)
    val AndroidTestDeps = listOf(
        testExtJUnit,
        espressoCore,
        coreTesting,
        espressoIntents,
        espressoContrib,
        uiautomator
    )

    // TestImplementation
    val TestDeps = listOf(
        junit,
        coreTesting,
        junitJupiter,
        kotest,
        mock,
        leakCanary
    )
}
