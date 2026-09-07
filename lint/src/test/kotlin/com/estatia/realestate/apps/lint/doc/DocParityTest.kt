package com.estatia.realestate.apps.lint.doc

import com.android.tools.lint.client.api.LintClient
import com.android.tools.lint.detector.api.TextFormat
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.registry.EstatiaIssueRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class DocParityTest {

    @Before
    fun setUp() {
        try {
            LintClient.clientName = "EstatiaTest"
        } catch (e: Exception) {
            // Already initialized or not allowed to set
        }
    }

    @Test
    fun `README law table must stay in sync with Law enum`() {
        val readmeFile = File("README.md")
        if (!readmeFile.exists()) {
            println("README.md not found, skipping test.")
            return
        }

        val readmeContent = readmeFile.readText()
        
        // Match: | **LAW-001** | ... | `TYPE` | ... |
        val rowRegex = Regex("""\| \*\*LAW-(\d+)\*\* \| [^|]+ \| `([^`]+)` \|""")
        val rowsInReadme = rowRegex.findAll(readmeContent).map { 
            "LAW-${it.groupValues[1]}" to it.groupValues[2] 
        }.toMap()

        val lawIdsInEnum = Law.entries.map { it.id }.toSet()
        val lawIdsInReadme = rowsInReadme.keys

        val missingInReadme = lawIdsInEnum - lawIdsInReadme
        val missingInEnum = lawIdsInReadme - lawIdsInEnum

        assertTrue(
            "The following Laws are defined in code (Law enum) but missing from README.md table:\n" +
            missingInReadme.joinToString("\n"),
            missingInReadme.isEmpty()
        )

        assertTrue(
            "The following Laws are documented in README.md but missing from code (Law enum):\n" +
            missingInEnum.joinToString("\n"),
            missingInEnum.isEmpty()
        )

        // Verify Types match
        Law.entries.forEach { law ->
            val readmeType = rowsInReadme[law.id]
            assertEquals(
                "Type mismatch for ${law.id} in README.md",
                law.type.name,
                readmeType
            )
        }
    }

    @Test
    fun `all registered issues must have a documented Law and correct metadata`() {
        val registry = EstatiaIssueRegistry()
        val issues = registry.issues
        
        val readmeFile = File("README.md")
        if (!readmeFile.exists()) return
        
        val readmeContent = readmeFile.readText()

        issues.forEach { issue ->
            // Skip non-Estatia issues if any
            val explanation = issue.getExplanation(TextFormat.TEXT)
            if (!explanation.contains("Architecture Law", ignoreCase = true)) return@forEach

            // Verify that the issue ID is mentioned in the "Enforcement Rule" column of the README
            assertTrue(
                "Issue '${issue.id}' is registered in EstatiaIssueRegistry but not mentioned in README.md 'Enforcement Rule' column.",
                readmeContent.contains("`${issue.id}`") || readmeContent.contains(issue.id)
            )

            // Verify that the explanation contains the Law ID
            val lawMatch = Regex("""Architecture Law: (LAW-\d+)""", RegexOption.IGNORE_CASE).find(explanation)
            assertTrue(
                "Issue '${issue.id}' has an invalid or missing 'Architecture Law' metadata in its explanation. Found: $explanation",
                lawMatch != null
            )
            
            val lawId = lawMatch!!.groupValues[1].uppercase()
            val law = Law.entries.find { it.id == lawId }
            
            assertTrue(
                "Issue '${issue.id}' references Law '$lawId' which is not in the Law enum.",
                law != null
            )

            // Verify that the explanation contains the correct Type
            assertTrue(
                "Issue '${issue.id}' explanation should contain 'Type: ${law!!.type.name}'",
                explanation.contains("Type: ${law.type.name}")
            )
        }
    }
}
