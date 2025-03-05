@Suppress("constPropertyName", "MemberVisibilityCanBePrivate")
object SonarQubeConfig {
    const val projectKey = "harrykibet_RealEstateApp2"
    const val organization = "harrykibet"
    const val hostUrl = "https://sonarcloud.io"
    const val sourceEncoding = "UTF-8"
    const val sources = "src/main"
    const val androidVariant = "release"
    const val exclusions = "**/build/**, **/generated/**"
    const val tests = "src/test"

    val javaBinaries: String
        get() = "build/intermediates/javac/release/classes"

    val junitReportPaths: String
        get() = "build/test-results/testDebugUnitTest"

    val jacocoReportPaths: String
        get() = "build/reports/jacoco/testDebugUnitTestCoverage/testDebugUnitTestCoverage.xml"
}
