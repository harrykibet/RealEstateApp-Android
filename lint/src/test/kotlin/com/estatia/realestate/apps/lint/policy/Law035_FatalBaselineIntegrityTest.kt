package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.LintClient
import com.android.tools.lint.detector.api.Severity
import com.estatia.realestate.apps.lint.registry.EstatiaIssueRegistry
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * LAW-035: Fatal Baseline Integrity.
 * 
 * This test ensures that any issue marked as FATAL in our EstatiaIssueRegistry 
 * is NOT allowed to be present in the lint-baseline.xml file.
 */
class Law035_FatalBaselineIntegrityTest {

    @Before
    fun setUp() {
        try {
            LintClient.clientName = "EstatiaTest"
        } catch (e: Exception) { }
    }

    @Test
    fun `FATAL issues must never be present in lint-baseline xml`() {
        val registry = EstatiaIssueRegistry()
        val fatalIssueIds = registry.issues
            .filter { it.defaultSeverity == Severity.FATAL }
            .map { it.id }
            .toSet()

        val baselineFile = File("../lint-baseline.xml")
        if (!baselineFile.exists()) {
            println("Skipping check: No lint-baseline.xml found at root.")
            return
        }

        val baselineContent = baselineFile.readText()
        
        val foundFatalIssues = fatalIssueIds.filter { id ->
            baselineContent.contains("id=\"$id\"")
        }

        assertTrue(
            "Governance Violation: The following FATAL architectural laws were found in the baseline file.\n" +
            "FATAL rules must never enter main and cannot be baselined (LAW-035):\n" +
            foundFatalIssues.joinToString("\n"),
            foundFatalIssues.isEmpty()
        )
    }
}
