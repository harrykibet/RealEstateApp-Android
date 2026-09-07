package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.Law
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * LAW-034: Lint Canary Regression.
 * 
 * This test runs against the ':core:canary-violations' module which contains 
 * deliberate violations of architectural laws.
 */
class Law034_LintCanaryRegressionTest {

    @Test
    fun `canary module must fire all expected architectural violations`() {
        // Attempt to find the report file by traversing up from the current directory
        var currentDir = File(".").absoluteFile
        var rootDir: File? = null
        
        while (currentDir != null) {
            if (File(currentDir, "settings.gradle.kts").exists()) {
                rootDir = currentDir
                break
            }
            currentDir = currentDir.parentFile
        }

        val reportFile = if (rootDir != null) {
            File(rootDir, "core/canary-violations/build/reports/lint-results.txt")
        } else {
            // Fallback to relative path if root not found
            File("../../core/canary-violations/build/reports/lint-results.txt")
        }

        assertTrue(
            "Governance Violation (${Law.LAW_034.id}): Canary lint report was not generated at expected path: ${reportFile.absolutePath}. " +
            "The enforcement infrastructure might be broken. Run './gradlew :core:canary-violations:lintDemoDebug' first.",
            reportFile.exists()
        )

        val reportText = reportFile.readText()
        
        val expectedIssues = listOf(
            "MissingResultWrapper",
            "ComposeArchitectureLeakage",
            "BusinessLogicInCompose",
            "LifecycleLeak",
            "SensitiveLogging",
            "SecretConcurrency",
            "HardcodedDispatcher",
            "FeatureCouplingViolation",
            "InfrastructureLeakage",
            "MissingVisibilityModifier",
            "ThreadSafetyViolation",
            "BlockingMainThreadWork",
            "ComposeMutableSingletonRead"
        )

        val missingIssues = expectedIssues.filterNot { reportText.contains(it) }

        assertTrue(
            "${Law.LAW_034.id}: ${Law.LAW_034.description}. The following architectural laws were NOT detected in the canary module:\n" +
            missingIssues.joinToString("\n") + 
            "\n\nFull Report Path: ${reportFile.absolutePath}",
            missingIssues.isEmpty()
        )
    }
}
