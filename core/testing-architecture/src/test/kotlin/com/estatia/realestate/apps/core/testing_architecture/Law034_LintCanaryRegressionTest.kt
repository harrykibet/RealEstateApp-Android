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
        // Deterministic path defined in :core:canary-violations build.gradle.kts
        val reportFile = File("../../core/canary-violations/build/reports/lint-results.txt")

        assertTrue(
            "Governance Violation (${Law.LAW_034.id}): Canary lint report was not generated. " +
            "The enforcement infrastructure might be broken. Run './gradlew :core:canary-violations:lint' first.",
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
            "MissingVisibilityModifier"
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
