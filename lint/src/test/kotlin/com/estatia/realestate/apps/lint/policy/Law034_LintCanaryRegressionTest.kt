package com.estatia.realestate.apps.lint.policy

import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.registry.EstatiaPolicyGroups
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * LAW-034: Lint Canary Regression.
 * 
 * Dynamic verification that EVERY registered Estatia Lint rule has an 
 * active violation in the ':core:canary-violations' module.
 */
class Law034_LintCanaryRegressionTest {

    @Test
    fun `canary module must fire all registered architectural violations`() {
        val reportPath = System.getProperty("CANARY_LINT_REPORT")
        assertNotNull("Governance Violation: System property 'CANARY_LINT_REPORT' is not set.", reportPath)

        val reportFile = File(reportPath!!)
        assertTrue(
            "Governance Violation (${Law.LAW_034.id}): Canary lint report was not found at: ${reportFile.absolutePath}.",
            reportFile.exists()
        )

        val reportText = reportFile.readText()
        
        // 🧪 Dynamic Source of Truth: Collect every registered issue ID
        val expectedIssues = EstatiaPolicyGroups.all
            .map { it.id }
            .filterNot { id ->
                // Exclude meta-detectors and heartbeat rules
                id == "CanaryHeartbeat" || id == "LintCanaryActive" || id == "SuppressionPolicyViolation"
            }
            .toSet()

        val missingIssues = expectedIssues.filterNot { reportText.contains(it) }

        assertTrue(
            "Architectural Regression (${Law.LAW_034.id}): The following registered Lint rules were NOT detected in the canary module. " +
            "Every rule implementation MUST have a corresponding violation in CanaryViolations.kt to prevent silent regressions:\n\n" +
            missingIssues.joinToString("\n") + 
            "\n\nTotal Expected: ${expectedIssues.size}\nMissing: ${missingIssues.size}",
            missingIssues.isEmpty()
        )
    }
}
