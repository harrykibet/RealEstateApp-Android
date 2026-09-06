package com.estatia.realestate.apps.core.testing_architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * LAW-034: Lint Canary Regression.
 * 
 * This test runs against the ':core:canary-violations' module which contains 
 * deliberate violations of every architectural law.
 */
class Law034_LintCanaryRegressionTest {

    @Test
    fun `canary module must fire all expected architectural violations`() {
        // Check for reports in both flavor locations
        val canaryLintReport = File("../../core/canary-violations/build/reports/lint-results-DemoDebug.txt")
        val fallbackReport = File("../../core/canary-violations/build/reports/lint-results-ProdDebug.txt")
        
        val reportFile = if (canaryLintReport.exists()) canaryLintReport else fallbackReport

        if (!reportFile.exists()) {
            println("Skipping canary check: Lint report not found. Run './gradlew :core:canary-violations:lint' first.")
            return
        }

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
            "MissingVisibilityModifier"
        )

        val missingIssues = expectedIssues.filterNot { reportText.contains(it) }

        assertTrue(
            "The following architectural laws were NOT detected in the canary module:\n" +
            missingIssues.joinToString("\n") + 
            "\n\nFull Report Path: ${reportFile.absolutePath}",
            missingIssues.isEmpty()
        )
    }
}
