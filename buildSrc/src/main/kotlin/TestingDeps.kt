/**
 * Object containing dependencies commonly used for testing in Android and Kotlin projects.
 *
 * This object provides a centralized location for managing test dependencies, including:
 * - **AndroidX Test:** JUnit extension, Espresso (core, intents, contrib), UI Automator.
 * - **JUnit:** Core JUnit library, and Jupiter (JUnit 5).
 * - **Architecture Components:** Core testing utilities.
 * - **Third-party testing libraries:** Truth (assertions), Kotest (testing framework), MockK (mocking), LeakCanary (memory leak detection).
 *
 * It also provides helper functions to easily retrieve lists of dependencies for different test types.
 */
@Suppress("MemberVisibilityCanBePrivate")
object TestingDeps {
    val testExtJUnit = Dependency.VersionedDependency(
        group = "androidx.test.ext",
        name = "junit",
        version = Versions.testExtJunit
    ).toGradleNotation

    val espressoCore = Dependency.VersionedDependency(
        group = "androidx.test.espresso",
        name = "espresso-core",
        version = Versions.espressoCore
    ).toGradleNotation

    val espressoIntents = Dependency.VersionedDependency(
        group = "androidx.test.espresso",
        name = "espresso-intents",
        version = Versions.espressoIntents
    ).toGradleNotation

    val espressoContrib = Dependency.VersionedDependency(
        group = "androidx.test.espresso",
        name = "espresso-contrib",
        version = Versions.espressoContrib
    ).toGradleNotation

    val junit = Dependency.VersionedDependency(
        group = "junit",
        name = "junit",
        version = Versions.junit
    ).toGradleNotation

    val coreTesting = Dependency.VersionedDependency(
        group = "androidx.arch.core",
        name = "core-testing",
        version = Versions.coreTesting
    ).toGradleNotation

    val junitJupiter = Dependency.VersionedDependency(
        group = "org.junit.jupiter",
        name = "junit-jupiter",
        version = Versions.jupiter
    ).toGradleNotation

    val googleTruth = Dependency.VersionedDependency(
        group = "com.google.truth",
        name = "truth",
        version = Versions.truth
    ).toGradleNotation

    val kotest = Dependency.VersionedDependency(
        group = "io.kotest",
        name = "kotest-runner-junit5",
        version = Versions.kotest
    ).toGradleNotation

    val mock = Dependency.VersionedDependency(
        group = "io.mockk",
        name = "mockk",
        version = Versions.mockk
    ).toGradleNotation

    val leakCanary = Dependency.VersionedDependency(
        group = "com.squareup.leakcanary",
        name = "leakcanary-android-instrumentation",
        version = Versions.leakCanary
    ).toGradleNotation

    val uiautomator = Dependency.VersionedDependency(
        group = "androidx.test.uiautomator",
        name = "uiautomator",
        version = Versions.uiAutomator
    ).toGradleNotation

    // Function to Retrieve Android Test Dependencies
    fun getAndroidTestDeps() = listOf(
        testExtJUnit,
        espressoCore,
        coreTesting,
        espressoIntents,
        espressoContrib,
        uiautomator
    )

    // Function to Retrieve Test Dependencies
    fun getTestDeps() = listOf(
        junit,
        coreTesting,
        junitJupiter,
        kotest,
        mock,
        leakCanary
    )
}
