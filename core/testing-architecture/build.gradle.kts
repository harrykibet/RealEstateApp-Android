plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

dependencies {
    implementation(projects.core.architecture)
    implementation(libs.konsist)
    testImplementation(libs.junit.junit)
}

tasks.withType<Test>().configureEach {
    // 🛡️ Bootstrapping Guarantee: The architecture tests MUST have access to the canary report.
    // Declare explicit dependency to ensure it runs before this module's tests.
    dependsOn(":core:canary-violations:lintDemoDebug")

    // Pass the absolute path to the report to avoid brittle relative path traversal in tests.
    systemProperty("CANARY_LINT_REPORT", "${project.rootDir}/core/canary-violations/build/reports/lint-results.txt")
}
