package com.estatia.realestate.apps.lint.doc

import com.android.tools.lint.client.api.LintClient
import com.android.tools.lint.detector.api.TextFormat
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.registry.EstatiaIssueRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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

    private fun getReadmeFile(): File {
        val readmePath = System.getProperty("LINT_README_PATH")
        assertNotNull(
            "Governance Violation: System property 'LINT_README_PATH' is not set. " +
            "This test must be run via Gradle to ensure correct environment setup.",
            readmePath
        )
        val file = File(readmePath)
        assertTrue(
            "Governance Violation: README.md not found at expected path: ${file.absolutePath}",
            file.exists()
        )
        return file
    }

    @Test
    fun `README law table must stay in sync with Law enum`() {
        val readmeFile = getReadmeFile()
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
    fun `bidirectional parity between registry and readme`() {
        val registry = EstatiaIssueRegistry()
        val registeredIssueIds = registry.issues.map { it.id }.toSet()
        
        val readmeFile = getReadmeFile()
        val readmeContent = readmeFile.readText()

        // 1. Every registered issue must appear in the README with L: prefix
        registry.issues.forEach { issue ->
            // Skip non-Estatia issues if any
            val explanation = issue.getExplanation(TextFormat.TEXT)
            if (!explanation.contains("Architecture Law", ignoreCase = true)) return@forEach

            assertTrue(
                "Issue '${issue.id}' is registered in EstatiaIssueRegistry but not mentioned in README.md 'Enforcement Rule' column with 'L:' prefix.",
                readmeContent.contains("`L:${issue.id}`") || readmeContent.contains("L:${issue.id}")
            )

            // Verify metadata in explanation matches Law enum
            val lawMatch = Regex("""Architecture Law: (LAW-\d+)""", RegexOption.IGNORE_CASE).find(explanation)
            assertTrue("Issue '${issue.id}' has missing Law metadata.", lawMatch != null)
            val lawId = lawMatch!!.groupValues[1].uppercase()
            val law = Law.entries.find { it.id == lawId }
            assertNotNull("Issue '${issue.id}' references unknown Law '$lawId'.", law)
            assertTrue("Issue '${issue.id}' explanation has wrong Law Type.", explanation.contains("Type: ${law!!.type.name}"))
        }

        // 2. Every L: rule ID in the README must correspond to a registered issue
        // Match the "Enforcement Rule" column: | LAW-XXX | Description | Type | `L:Rule1`, `K:Rule2` |
        val ruleColumnRegex = Regex("""\| \*\*LAW-\d+\*\* \| [^|]+ \| [^|]+ \| ([^|]+) \|""")
        val mentionedLintRules = ruleColumnRegex.findAll(readmeContent).flatMap { match ->
            match.groupValues[1].split(",")
                .map { it.trim().removeSurrounding("`") }
                .filter { it.startsWith("L:") }
                .map { it.removePrefix("L:") }
        }.toSet()

        val undocumentedInRegistry = mentionedLintRules - registeredIssueIds
        assertTrue(
            "The following Lint rules are documented in README.md (with L: prefix) but have NO implementation in EstatiaIssueRegistry:\n" +
            undocumentedInRegistry.joinToString("\n"),
            undocumentedInRegistry.isEmpty()
        )
    }
}
