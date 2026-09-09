package com.estatia.realestate.apps.lint.policy

import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.registry.EstatiaPolicyGroups
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * LAW-034: High-Fidelity Lint Canary Regression.
 * 
 * Verifies that architectural rules detect the EXACT code constructs 
 * they are intended for, and do NOT flag valid patterns (Negative Canaries).
 */
class Law034_LintCanaryRegressionTest {

    private val canaryModulePath = "core/canary-violations"
    
    data class CanaryExpectation(
        val issueId: String,
        val line: Int,
        val file: File,
        val isPositive: Boolean
    )

    @Test
    fun `canary module must satisfy the High-Fidelity Enforcement Oracle`() {
        val reportPath = System.getProperty("CANARY_LINT_XML_REPORT") ?: 
            "C:/Users/Administrator/StudioProjects/RealEstateApp-Android/core/canary-violations/build/reports/lint-results.xml"
        
        val reportFile = File(reportPath)
        assertTrue("Canary lint report not found at $reportPath", reportFile.exists())

        // 1. Build expectations from source code markers
        val expectations = collectExpectations()
        
        println("DEBUG: Expectations:")
        expectations.take(10).forEach { println("  - ${it.issueId} expected at ${it.file.name}:${it.line}") }
        
        // 2. Parse actual violations from XML
        val actualViolations = parseActualViolations(reportFile)
        
        println("DEBUG: Actual Violations Found:")
        actualViolations.forEach { println("  - ${it.issueId} at ${it.file.name}:${it.line}") }

        // 3. Verify Positive Canaries (Must exist within +/- 5 line range)
        val missingPositives = expectations.filter { it.isPositive }.filterNot { exp ->
            actualViolations.any { act -> 
                act.issueId == exp.issueId && 
                Math.abs(act.line - exp.line) <= 5 && 
                act.file.name.equals(exp.file.name, ignoreCase = true) 
            }
        }

        // 4. Verify Negative Canaries (Must NOT exist within +/- 5 line range)
        val falsePositives = expectations.filter { !it.isPositive }.filter { exp ->
            actualViolations.any { act -> 
                act.issueId == exp.issueId && 
                Math.abs(act.line - exp.line) <= 5 && 
                act.file.name.equals(exp.file.name, ignoreCase = true) 
            }
        }

        // 5. Build Error Message
        val errorMessage = StringBuilder()
        if (missingPositives.isNotEmpty()) {
            errorMessage.append("\n❌ MISSING POSITIVE CANARIES (Regression detected):\n")
            missingPositives.forEach { 
                errorMessage.append("  - ${it.issueId} at ${it.file.name}:${it.line}\n") 
            }
        }

        if (falsePositives.isNotEmpty()) {
            errorMessage.append("\n❌ FAILED NEGATIVE CANARIES (False positive detected):\n")
            falsePositives.forEach { 
                errorMessage.append("  - ${it.issueId} incorrectly flagged at ${it.file.name}:${it.line}\n") 
            }
        }

        // 6. Verify full rule coverage (Are all registered rules represented in canary?)
        val registeredIds = EstatiaPolicyGroups.all
            .map { it.id }
            .filterNot { it == "CanaryHeartbeat" || it == "LintCanaryActive" || it == "SuppressionPolicyViolation" }
            .toSet()
        
        val representedIds = expectations.map { it.issueId }.toSet()
        val undocumentedRules = registeredIds - representedIds

        if (undocumentedRules.isNotEmpty()) {
            errorMessage.append("\n⚠️ UNDOCUMENTED RULES (No canary violation/pass defined):\n")
            undocumentedRules.forEach { errorMessage.append("  - $it\n") }
        }

        if (errorMessage.isNotEmpty()) {
            fail("High-Fidelity Canary Failure (LAW-034):$errorMessage")
        }
    }

    private fun collectExpectations(): List<CanaryExpectation> {
        val srcDir = File("C:/Users/Administrator/StudioProjects/RealEstateApp-Android/$canaryModulePath/src/main/kotlin")
        val results = mutableListOf<CanaryExpectation>()
        
        srcDir.walkTopDown().filter { it.extension == "kt" }.forEach { file ->
            file.readLines().forEachIndexed { index, line ->
                val lineNumber = index + 1
                
                // Match: [CANARY:POSITIVE:IssueId]
                Regex("""\[CANARY:POSITIVE:([^\]]+)\]""").findAll(line).forEach { match ->
                    results.add(CanaryExpectation(match.groupValues[1], lineNumber, file, true))
                }
                
                // Match: [CANARY:NEGATIVE:IssueId]
                Regex("""\[CANARY:NEGATIVE:([^\]]+)\]""").findAll(line).forEach { match ->
                    results.add(CanaryExpectation(match.groupValues[1], lineNumber, file, false))
                }
            }
        }
        return results
    }

    private data class ActualViolation(val issueId: String, val line: Int, val file: File)

    private fun parseActualViolations(xmlFile: File): List<ActualViolation> {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(xmlFile)
        val issues = doc.getElementsByTagName("issue")
        val results = mutableListOf<ActualViolation>()

        for (i in 0 until issues.length) {
            val element = issues.item(i) as Element
            val id = element.getAttribute("id")
            val location = element.getElementsByTagName("location").item(0) as Element
            val fileAttr = location.getAttribute("file")
            val lineAttr = location.getAttribute("line")
            
            if (lineAttr.isNotEmpty()) {
                results.add(ActualViolation(id, lineAttr.toInt(), File(fileAttr)))
            }
        }
        return results
    }
}
