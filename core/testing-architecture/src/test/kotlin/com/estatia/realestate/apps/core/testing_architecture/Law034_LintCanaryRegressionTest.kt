package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.Law
import org.junit.Assert.assertNotNull
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
        val reportPath = System.getProperty("CANARY_LINT_REPORT")
        
        assertNotNull(
            "Governance Violation: System property 'CANARY_LINT_REPORT' is not set. " +
            "This test must be run via Gradle to ensure correct environment setup.",
            reportPath
        )

        val reportFile = File(reportPath)

        assertTrue(
            "Governance Violation (${Law.LAW_034.id}): Canary lint report was not found at: ${reportFile.absolutePath}. " +
            "The enforcement infrastructure might be broken. Ensure ':core:canary-violations:lintDemoDebug' is running.",
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
            "ComposeMutableSingletonRead",
            "HardcodedSecrets",
            "BackingPropertyConvention"
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
