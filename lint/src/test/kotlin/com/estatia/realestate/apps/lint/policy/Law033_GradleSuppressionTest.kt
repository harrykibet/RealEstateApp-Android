package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.LintClient
import com.android.tools.lint.detector.api.Severity
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.registry.EstatiaIssueRegistry
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * LAW-033: Suppression Policy Enforcement (Gradle Layer).
 * 
 * Ensures that build.gradle.kts files do not globally disable critical lint checks.
 * This test dynamically derives protected issues from the EstatiaIssueRegistry.
 */
class Law033_GradleSuppressionTest {

    @Before
    fun setUp() {
        try {
            LintClient.clientName = "EstatiaTest"
        } catch (_: Exception) { }
    }

    @Test
    fun `gradle files must not globally disable architectural or security lint checks`() {
        val registry = EstatiaIssueRegistry()
        val forbiddenGlobalDisables = registry.issues
            .filter { it.defaultSeverity == Severity.FATAL }
            .map { it.id }
            .toSet()

        // From 'lint' module directory, '..' is the project root
        val rootDir = File("..") 
        val gradleFiles = rootDir.walkTopDown()
            .filter { it.name == "build.gradle.kts" || it.name == "LintConventionPlugin.kt" }
            .filter { !it.path.contains(".gradle") && !it.path.contains("build") }
            .toList()

        val violations = mutableListOf<String>()

        gradleFiles.forEach { file ->
            val content = file.readText()
            forbiddenGlobalDisables.forEach { id ->
                // Matches both disable.add("ID") and disable += "ID" (and variants with whitespace)
                val regex = Regex("""disable(\.add|\s*\+=\s*)\s*\(?\s*["']$id["']\s*\)?""")
                if (regex.containsMatchIn(content)) {
                    violations.add("${file.path}: Found global disable of '$id'")
                }
            }
        }

        assertTrue(
            "${Law.LAW_033.id}: ${Law.LAW_033.description}. Use @Suppress with justification in source instead:\n" +
            violations.joinToString("\n"),
            violations.isEmpty()
        )
    }
}
