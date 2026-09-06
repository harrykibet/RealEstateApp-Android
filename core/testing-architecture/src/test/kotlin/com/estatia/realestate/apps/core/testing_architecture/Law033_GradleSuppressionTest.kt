package com.estatia.realestate.apps.core.testing_architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * LAW-033: Suppression Policy Enforcement (Gradle Layer).
 * 
 * Ensures that build.gradle.kts files do not globally disable critical lint checks.
 * All suppressions must be local (in source) with a "Justification:" comment.
 */
class Law033_GradleSuppressionTest {

    private val forbiddenGlobalDisables = setOf(
        "UnsafeOptInUsageError",
        "TrustAllX509TrustManager",
        "ExposedMutableState",
        "ForbiddenCoroutineScope",
        "HardcodedDispatcher",
        "MissingResultWrapper"
    )

    @Test
    fun `gradle files must not globally disable architectural or security lint checks`() {
        val rootDir = File("../../") // Navigate to project root from core/testing-architecture
        val gradleFiles = rootDir.walkTopDown()
            .filter { it.name == "build.gradle.kts" || it.name == "LintConventionPlugin.kt" }
            .toList()

        val violations = mutableListOf<String>()

        gradleFiles.forEach { file ->
            val content = file.readText()
            forbiddenGlobalDisables.forEach { id ->
                if (content.contains("disable.add(\"$id\")") || content.contains("disable += \"$id\"")) {
                    violations.add("${file.path}: Found global disable of '$id'")
                }
            }
        }

        assertTrue(
            "Global suppressions detected in Gradle files (LAW-033). Use @Suppress with justification in source instead:\n" +
            violations.joinToString("\n"),
            violations.isEmpty()
        )
    }
}
