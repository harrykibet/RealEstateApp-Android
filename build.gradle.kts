import com.jraska.module.graph.assertion.GraphRulesExtension
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
    description = "Authoritative Release Hardening Audit. Verifies release binaries against Estatia Law LAW-038A-E."
    group = "verification"
    
    doLast {
        val appModule = project.findProject(":app") ?: return@doLast
        val rootDir = project.rootDir
        
        // --- LAW-038A: Release minification contract ---
        // We check the build configuration if possible, or build artifacts.
        val releaseMappingDir = File(rootDir, "app/build/outputs/mapping/prodRelease")
        if (releaseMappingDir.exists()) {
            val mappingFile = File(releaseMappingDir, "mapping.txt")
            if (!mappingFile.exists()) {
                throw GradleException("LAW-038A Violation: Release mapping.txt missing. Minification might be disabled for prodRelease.")
            }
        }

        // --- LAW-038B: R8 mapping integrity ---
        val mappingFile = File(rootDir, "app/build/outputs/mapping/prodRelease/mapping.txt")
        if (mappingFile.exists()) {
            val content = mappingFile.readText()
            val sensitiveKeywords = listOf("InternalImpl", "SecretStore", "DebugConfig")
            val violations = sensitiveKeywords.filter { content.contains(it) }
            if (violations.isNotEmpty()) {
                throw GradleException("LAW-038B Violation: Sensitive internal symbols found in R8 mapping: $violations. Update Proguard rules to obfuscate these.")
            }
        }

        // --- LAW-038C: Secret/string leak detection ---
        val rawSecrets = listOf("AKIA", "AIza", "AAAA") // Common cloud provider prefixes
        // We scan the mapping file as a proxy for the binary strings
        if (mappingFile.exists()) {
            val content = mappingFile.readText()
            val foundSecrets = rawSecrets.filter { content.contains(it) }
            if (foundSecrets.isNotEmpty()) {
                throw GradleException("LAW-038C Violation: Potential hardcoded secrets detected in release metadata: $foundSecrets")
            }
        }

        // --- LAW-038D: Forbidden debug artifact detection ---
        val forbiddenArtifacts = listOf("leakcanary", "stetho", "timber.log.Timber\$DebugTree")
        if (mappingFile.exists()) {
            val content = mappingFile.readText()
            val foundForbidden = forbiddenArtifacts.filter { content.contains(it) }
            if (foundForbidden.isNotEmpty()) {
                throw GradleException("LAW-038D Violation: Debug-only infrastructure detected in release binary: $foundForbidden")
            }
        }

        // --- LAW-038E: Release binary policy ---
        val releaseApkDir = File(rootDir, "app/build/outputs/apk/prod/release")
        val releaseBundleDir = File(rootDir, "app/build/outputs/bundle/prodRelease")
        
        val hasApk = releaseApkDir.exists() && (releaseApkDir.listFiles()?.any { it.name.endsWith(".apk") } == true)
        val hasBundle = releaseBundleDir.exists() && (releaseBundleDir.listFiles()?.any { it.name.endsWith(".aab") } == true)
        
        if (!hasApk && !hasBundle) {
            println("Skip LAW-038E: Release binary not found. This check requires a previous 'assembleProdRelease' or 'bundleProdRelease' run.")
        } else if (hasApk) {
            val apk = releaseApkDir.listFiles()?.find { it.name.endsWith(".apk") }
            if (apk != null && apk.length() > 50 * 1024 * 1024) { // 50MB threshold
                 println("WARNING (LAW-038E): Release APK size is unusually large: ${apk.length() / 1024 / 1024}MB")
            }
        }
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
