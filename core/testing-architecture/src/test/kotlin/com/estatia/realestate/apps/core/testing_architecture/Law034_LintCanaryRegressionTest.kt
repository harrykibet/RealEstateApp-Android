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
        
        // 🛡️ PRECONDITION: System property must be provided by Gradle
        assertNotNull(
            "Governance Violation: System property 'CANARY_LINT_REPORT' is not set. " +
            "This test must be run via Gradle to ensure correct environment setup.",
            reportPath
        )

        val reportFile = File(reportPath!!)

        // 🛡️ PRECONDITION: Report must exist. Failure here indicates infrastructure regression.
        assertTrue(
            "Governance Violation (${Law.LAW_034.id}): Canary lint report was not found at: ${reportFile.absolutePath}. " +
            "The enforcement infrastructure might be broken. Ensure ':core:canary-violations:lintDemoDebug' has run.",
            reportFile.exists()
        )

        val reportText = reportFile.readText()
        
        // Comprehensive list of detector IDs that MUST be firing in the canary module.
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
            "Architectural Regression (${Law.LAW_034.id}): The following expected violations were NOT detected in the canary module. " +
            "This suggests a regression in the enforcement engine (Lint/KSP/Config):\n" +
            missingIssues.joinToString("\n") + 
            "\n\nFull Report Path: ${reportFile.absolutePath}",
            missingIssues.isEmpty()
        )
    }
}
