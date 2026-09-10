import com.jraska.module.graph.assertion.GraphRulesExtension
import org.gradle.api.artifacts.ProjectDependency
import java.io.File

buildscript {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.com.android.application)  apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.plugin.compose) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
    alias(libs.plugins.com.google.gms.google.services) apply false
    alias(libs.plugins.com.google.firebase.crashlytics) apply false
    alias(libs.plugins.com.google.firebase.perf) apply false
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    alias(libs.plugins.androidx.room)  apply false
    alias(libs.plugins.org.jetbrains.dokka) apply false
    alias(libs.plugins.com.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    alias(libs.plugins.org.sonarqube) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.android.dynamic.feature) apply false
    alias(libs.plugins.module.graph)
    alias(libs.plugins.kotlin.serialization)  apply false
    id("com.estatia.realestate.apps.architecture")
}

extensions.configure<GraphRulesExtension>("moduleGraphAssert") {
    maxHeight = 10
    configurations = setOf("api", "implementation")

    restricted = arrayOf(
        ":core:(?!canary-violations).* -X> :feature.*",
        ":feature.* -X> :feature:(?!shared-ui).*",
        ":core:domain -X> :core:(network|database|datastore|intelligence|notifications|security)",
        ":core:model -X> :core:(?!common).*"
    )
}

tasks.register("checkDependencyDrift") {
    description = "Checks for hardcoded dependencies."
    group = "verification"
    doLast {
        val rootDir = project.projectDir
        val magicStringRegex = Regex("""(implementation|api|testImplementation|debugImplementation|androidTestImplementation)\s*\(?\s*["']([^:"]+:[^:"]+:[^:"]+)["']""")
        val violations = mutableListOf<String>()
        rootDir.walkTopDown().forEach { file ->
            if (file.name == "build.gradle.kts" && !file.path.contains(".gradle") && !file.path.contains("build-logic")) {
                val content = file.readText()
                magicStringRegex.findAll(content).forEach { violations.add("${file.relativeTo(rootDir).path}: ${it.value}") }
            }
        }
        if (violations.isNotEmpty()) {
            throw GradleException("LAW-037: Dependency Drift detected. Hardcoded dependencies found:\n" + violations.joinToString("\n"))
        }
    }
}

tasks.register("auditBinaryPurity") {
    description = "Audits the binary for sensitive keywords."
    group = "verification"
    doLast {
        val mappingFile = File(project.rootDir, "app/build/outputs/mapping/prodRelease/mapping.txt")
        if (!mappingFile.exists()) {
            println("Skip: R8 mapping not found at ${mappingFile.absolutePath}")
            return@doLast
        }
        val forbidden = listOf("Secret", "ApiKey", "InternalImpl")
        val content = mappingFile.readText()
        val violations = forbidden.filter { content.contains(it) }
        if (violations.isNotEmpty()) throw GradleException("LAW-038: Binary Purity Violation. Sensitive keywords found in R8 mapping: $violations")
    }
}

tasks.register("verifyArchitecture") {
    group = "verification"
    description = "Executes all architectural and policy enforcement gates."

    // 1. Standard Implementation Guard (Lint)
    dependsOn(":lint:assemble") // Ensure custom lint rules are built
    dependsOn(":lint:test")     // Run detector unit tests and DocParityTest

    // 2. Global Structural Enforcement (Konsist & Regression Tests)
    dependsOn(":core:testing-architecture:test")
    dependsOn(":core:canary-violations:lintDemoDebug")
    dependsOn("assertModuleGraph")
    dependsOn("generateModuleGraphs")
    dependsOn(":core:data:compileDemoDebugKotlin")
    dependsOn(":core:network:compileDemoDebugKotlin")
    dependsOn("checkDependencyDrift")
}
