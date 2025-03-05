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
    )

    val espressoCore = Dependency.VersionedDependency(
        group = "androidx.test.espresso",
        name = "espresso-core",
        version = Versions.espressoCore
    )

    val espressoIntents = Dependency.VersionedDependency(
        group = "androidx.test.espresso",
        name = "espresso-intents",
        version = Versions.espressoIntents
    )

    val espressoContrib = Dependency.VersionedDependency(
        group = "androidx.test.espresso",
        name = "espresso-contrib",
        version = Versions.espressoContrib
    )

    val junit = Dependency.VersionedDependency(
        group = "junit",
        name = "junit",
        version = Versions.junit
    )

    val coreTesting = Dependency.VersionedDependency(
        group = "androidx.arch.core",
        name = "core-testing",
        version = Versions.coreTesting
    )

    val junitJupiter = Dependency.VersionedDependency(
        group = "org.junit.jupiter",
        name = "junit-jupiter",
        version = Versions.jupiter
    )

    val googleTruth = Dependency.VersionedDependency(
        group = "com.google.truth",
        name = "truth",
        version = Versions.truth
    )

    val kotest = Dependency.VersionedDependency(
        group = "io.kotest",
        name = "kotest-runner-junit5",
        version = Versions.kotest
    )

    val mock = Dependency.VersionedDependency(
        group = "io.mockk",
        name = "mockk",
        version = Versions.mockk
    )

    val leakCanary = Dependency.VersionedDependency(
        group = "com.squareup.leakcanary",
        name = "leakcanary-android-instrumentation",
        version = Versions.leakCanary
    )

    val uiautomator = Dependency.VersionedDependency(
        group = "androidx.test.uiautomator",
        name = "uiautomator",
        version = Versions.uiAutomator
    )

    // Function to Retrieve Android Test Dependencies
    fun getAndroidTestDeps() = listOf(
        testExtJUnit,
        espressoCore,
        coreTesting,
        espressoIntents,
        espressoContrib,
        uiautomator
    ).map { it.get() }

    // Function to Retrieve Test Dependencies
    fun getTestDeps() = listOf(
        junit,
        coreTesting,
        junitJupiter,
        kotest,
        mock,
        leakCanary
    ).map { it.get() }
}
