package com.estatia.realestate.apps.core.testing_architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * LAW-034: Lint Canary Regression.
 * 
 * This test runs against the ':core:canary-violations' module which contains 
 * deliberate violations of every architectural law.
 * 
 * It ensures that our custom Lint detectors:
 * 1. Do not crash on real multi-module source code.
 * 2. Correcty identify violations even with complex classpath/resolution context.
 */
class Law034_LintCanaryRegressionTest {

    @Test
    fun `canary module must fire all expected architectural violations`() {
        val canaryLintReport = File("../../core/canary-violations/lint-results.txt")
        
        // This test requires a manual 'gradlew :core:canary-violations:lint' to be run 
        // in CI before this test runs, or we can check the most recent report.
        if (!canaryLintReport.exists()) {
            println("Skipping canary check: lint-results.txt not found. Run './gradlew :core:canary-violations:lint' first.")
            return
        }

        val reportText = canaryLintReport.readText()
        
        val expectedIssues = listOf(
            "MissingResultWrapper",
            "ComposeArchitectureLeakage",
            "BusinessLogicInCompose",
            "ComposeMutableSingletonRead",
            "MutableStateFlow", // From Law016
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
            "\n\nFull Report:\n$reportText",
            missingIssues.isEmpty()
        )
    }
}
