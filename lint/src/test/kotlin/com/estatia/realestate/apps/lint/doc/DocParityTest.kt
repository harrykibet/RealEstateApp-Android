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
        
        // 🛡️ PRECONDITION: System property must be provided by Gradle
        assertNotNull(
            "Governance Violation: System property 'LINT_README_PATH' is not set. " +
            "This test must be run via Gradle to ensure correct environment setup.",
            readmePath
        )
        
        val file = File(readmePath!!)
        
        // 🛡️ PRECONDITION: File must exist.
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
        
        // Match: | **LAW-001** | Description | `RISK` | `CONFIDENCE` | `ENFORCEMENT` | ... |
        val rowRegex = Regex("""\| \*\*LAW-(\d+)\*\* \| [^|]+ \| `([^`]+)` \| `([^`]+)` \| `([^`]+)` \|""")
        val rowsInReadme = rowRegex.findAll(readmeContent).map { 
            val id = "LAW-${it.groupValues[1]}"
            id to Triple(it.groupValues[2], it.groupValues[3], it.groupValues[4])
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

        // Verify Risk, Confidence, Enforcement match
        Law.entries.forEach { law ->
            val (readmeRisk, readmeConfidence, readmeEnforcement) = rowsInReadme[law.id]!!
            assertEquals(
                "Risk mismatch for ${law.id} in README.md",
                law.risk.name,
                readmeRisk
            )
            assertEquals(
                "Confidence mismatch for ${law.id} in README.md",
                law.confidence.name,
                readmeConfidence
            )
            assertEquals(
                "Enforcement mismatch for ${law.id} in README.md",
                law.enforcement.name,
                readmeEnforcement
            )
        }
    }

    @Test
    fun `bidirectional parity between registry and readme`() {
        val registry = EstatiaIssueRegistry()
        val registeredIssueIds = registry.issues.map { it.id }.toSet()
        
        val readmeFile = getReadmeFile()
        val readmeContent = readmeFile.readText()

        // 1. Every registered issue must appear in the README with L: or H: prefix
        registry.issues.forEach { issue ->
            // Skip non-Estatia issues if any
            val explanation = issue.getExplanation(TextFormat.TEXT)
            if (!explanation.contains("Architecture Law", ignoreCase = true)) return@forEach

            val isPresent = readmeContent.contains("`L:${issue.id}`") || 
                            readmeContent.contains("L:${issue.id}") ||
                            readmeContent.contains("`H:${issue.id}`") ||
                            readmeContent.contains("H:${issue.id}")

            assertTrue(
                "Issue '${issue.id}' is registered in EstatiaIssueRegistry but not mentioned in README.md 'Enforcement Rule' column with 'L:' or 'H:' prefix.",
                isPresent
            )

            // Verify metadata in explanation matches Law enum
            val lawMatch = Regex("""Architecture Law: (LAW-\d+)""", RegexOption.IGNORE_CASE).find(explanation)
            assertTrue("Issue '${issue.id}' has missing Law metadata.", lawMatch != null)
            val lawId = lawMatch!!.groupValues[1].uppercase()
            val law = Law.entries.find { it.id == lawId }
            assertNotNull("Issue '${issue.id}' references unknown Law '$lawId'.", law)
            
            assertTrue("Issue '${issue.id}' explanation has wrong Risk.", explanation.contains("Risk: ${law!!.risk.name}"))
            assertTrue("Issue '${issue.id}' explanation has wrong Confidence.", explanation.contains("Confidence: ${law.confidence.name}"))
            assertTrue("Issue '${issue.id}' explanation has wrong Enforcement.", explanation.contains("Enforcement: ${law.enforcement.name}"))
        }

        // 2. Every L: or H: rule ID in the README must correspond to a registered issue and correct Law
        // Match the "Enforcement Rule" column: | LAW-XXX | Description | Risk | Confidence | Enforcement | `L:Rule1`, `K:Rule2` |
        val rowsRegex = Regex("""\| \*\*(${Law.entries.joinToString("|") { it.id }})\*\* \| [^|]+ \| [^|]+ \| [^|]+ \| [^|]+ \| ([^|]+) \|""")
        
        val mentionedInReadme = mutableSetOf<String>()

        rowsRegex.findAll(readmeContent).forEach { match ->
            val lawId = match.groupValues[1]
            val ruleString = match.groupValues[2]
            val rules = ruleString.split(",")
                .map { it.trim().removeSurrounding("`") }
                .filter { it.startsWith("L:") || it.startsWith("H:") }
                .map { it.substring(2) }

            rules.forEach { ruleId ->
                mentionedInReadme.add(ruleId)
                val issue = registry.issues.find { it.id == ruleId }
                assertNotNull("Lint Rule '$ruleId' mentioned for $lawId in README not found in registry.", issue)
                
                val explanation = issue!!.getExplanation(TextFormat.TEXT)
                assertTrue(
                    "Semantic Mismatch: README links '$ruleId' to $lawId, but the issue metadata in code references a different law.\n" +
                    "Check the 'architectureLaw' parameter in the detector's EstatiaIssue.create() call.",
                    explanation.contains("Architecture Law: $lawId")
                )
            }
        }

        // 3. Every registered issue in code MUST be documented in the README for its Law
        val allIssuesInCode = registry.issues
        allIssuesInCode.forEach { issue ->
            val explanation = issue.getExplanation(TextFormat.TEXT)
            val lawMatch = Regex("""Architecture Law: (LAW-\d+)""").find(explanation) ?: return@forEach
            val lawId = lawMatch.groupValues[1]
            
            assertTrue(
                "Code -> README Violation: Lint Rule '${issue.id}' is linked to $lawId in code, but is NOT listed in the README table for that law.\n" +
                "Update the 'Enforcement Rule(s)' column for $lawId in lint/README.md.",
                readmeContent.contains("L:${issue.id}") || readmeContent.contains("H:${issue.id}")
            )
        }

        // 4. Verify that all L:/H: prefixes are actually in the registry (Reverse check)
        val undocumentedInRegistry = mentionedInReadme - registeredIssueIds
        assertTrue(
            "The following Lint rules are documented in README.md but have NO implementation in EstatiaIssueRegistry:\n" +
            undocumentedInRegistry.joinToString("\n"),
            undocumentedInRegistry.isEmpty()
        )
    }
}
