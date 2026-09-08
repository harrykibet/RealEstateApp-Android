plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // 🏗️ SSoT Dependency: MUST be bundled into the Lint JAR for crash resilience.
    implementation(projects.core.architecture)
    
    compileOnly(libs.android.lint.api)
    compileOnly(libs.android.lint.checks)

    testImplementation(libs.android.lint.api)
    testImplementation(libs.android.lint.checks)
    testImplementation(libs.android.lint.tests)
    testImplementation(libs.junit.junit)
    testImplementation(projects.core.architecture)
}

tasks.withType<Test>().configureEach {
    // 🛡️ Governance Properties: Enable dynamic verification of the entire architectural engine.
    
    // Pass the path to README.md for DocParityTest
    systemProperty("LINT_README_PATH", "${project.projectDir}/README.md")
    
    // Pass the path to the canary report for Law034
    systemProperty("CANARY_LINT_REPORT", "${project.rootDir}/core/canary-violations/build/reports/lint-results.txt")
    
    // Pass module paths for MetaArchitectureSyncTest
    systemProperty("KSP_SERVICES_PATH", "${project.rootDir}/core/ksp-architecture/src/main/resources/META-INF/services/com.google.devtools.ksp.processing.SymbolProcessorProvider")
    systemProperty("KONSIST_TESTS_PATH", "${project.rootDir}/core/testing-architecture/src/test/kotlin/com/estatia/realestate/apps/core/testing_architecture")
    systemProperty("PROJECT_ROOT", "${project.rootDir}")

    // Ensure canary report is generated before running tests that depend on it
    dependsOn(":core:canary-violations:lintDemoDebug")
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    manifest {
        attributes("Lint-Registry-v2" to "com.estatia.realestate.apps.lint.registry.EstatiaIssueRegistry")
    }
    
    // 🛡️ Shadow Logic: Bundle :core:architecture classes directly into the Lint JAR.
    // This prevents ClassNotFoundException when Lint runs in the IDE or standalone CLI.
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    val runtimeClasspath = configurations.runtimeClasspath
    from(runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
        exclude("META-INF/MANIFEST.MF")
    }
}
