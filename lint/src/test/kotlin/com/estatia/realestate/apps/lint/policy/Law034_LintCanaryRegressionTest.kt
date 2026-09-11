package com.estatia.realestate.apps.lint.policy

import com.estatia.realestate.apps.lint.registry.EstatiaPolicyGroups
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * LAW-034: High-Fidelity Lint Canary Regression.
 * 
 * authoritatively verifies that architectural rules detect the EXACT code constructs 
 * they are intended for, ensuring zero regressions and zero false positives.
 */
class Law034_LintCanaryRegressionTest {

    private val canaryModulePath = "core/canary-violations"
    private val projectRoot = "C:/Users/Administrator/StudioProjects/RealEstateApp-Android"
    
    data class CanaryExpectation(
        val issueId: String,
        val line: Int,
        val file: File,
        val relativePath: String,
        val isPositive: Boolean,
        val severity: String? = null,
        val messageToken: String? = null
    )

    @Test
    fun `canary module must satisfy the High-Fidelity Enforcement Oracle`() {
        val reportPath = System.getProperty("CANARY_LINT_XML_REPORT") ?: 
            "$projectRoot/core/canary-violations/build/reports/lint-results.xml"
        
        val reportFile = File(reportPath)
        assertTrue("Canary lint report not found at $reportPath", reportFile.exists())

        val registeredLintIds = EstatiaPolicyGroups.all
            .map { it.id }
            .toSet()

        val expectations = collectExpectations().filter { 
            registeredLintIds.contains(it.issueId) || it.issueId == "LintCanaryActive"
        }
        
        val actualViolations = parseActualViolations(reportFile)

        // 3. Verify Positive Canaries
        val missingPositives = expectations.filter { it.isPositive }.filterNot { exp ->
            val matches = actualViolations.filter { act -> 
                act.issueId == exp.issueId && 
                isPathMatch(act.file, exp.file) &&
                isLineMatch(act.line, exp.line)
            }
            
            val foundMatch = matches.any { act -> 
                isSeverityMatch(act.severity, exp.severity) &&
                isMessageMatch(act.message, exp.messageToken)
            }
            
            if (!foundMatch && exp.issueId != "LintCanaryActive") {
                println("DEBUG: Failed to match expectation: ${exp.issueId} at ${exp.relativePath}:${exp.line}")
                actualViolations.filter { it.issueId == exp.issueId }.forEach { act ->
                    println("  Candidate Actual: ${act.file.path}:${act.line} (Severity: ${act.severity}, Msg: ${act.message})")
                    println("    PathMatch: ${isPathMatch(act.file, exp.file)}")
                    println("    LineMatch: ${isLineMatch(act.line, exp.line)}")
                }
            }
            foundMatch
        }

        // 4. Verify Negative Canaries
        val falsePositives = expectations.filter { !it.isPositive }.filter { exp ->
            actualViolations.any { act -> 
                act.issueId == exp.issueId && 
                isPathMatch(act.file, exp.file) &&
                isLineMatch(act.line, exp.line)
            }
        }

        // 5. Build Error Message
        val errorMessage = StringBuilder()
        if (missingPositives.isNotEmpty()) {
            errorMessage.append("\n❌ MISSING POSITIVE CANARIES (Regression detected):\n")
            missingPositives.forEach { 
                errorMessage.append("  - ${it.issueId} at ${it.relativePath}:${it.line}")
                if (it.severity != null) errorMessage.append(" (Expected Severity: ${it.severity})")
                if (it.messageToken != null) errorMessage.append(" (Expected Token: ${it.messageToken})")
                errorMessage.append("\n")
            }
        }

        if (falsePositives.isNotEmpty()) {
            errorMessage.append("\n❌ FAILED NEGATIVE CANARIES (False positive detected):\n")
            falsePositives.forEach { 
                errorMessage.append("  - ${it.issueId} incorrectly flagged at ${it.relativePath}:${it.line}\n") 
            }
        }

        val registeredIdsForScan = registeredLintIds
            .filterNot { it == "CanaryHeartbeat" || it == "LintCanaryActive" || it == "SuppressionPolicyViolation" }
            .toSet()
        
        val representedIds = expectations.map { it.issueId }.toSet()
        val undocumentedRules = registeredIdsForScan - representedIds

        if (undocumentedRules.isNotEmpty()) {
            errorMessage.append("\n⚠️ UNDOCUMENTED RULES (No canary violation/pass defined):\n")
            undocumentedRules.forEach { errorMessage.append("  - $it\n") }
        }

        if (errorMessage.isNotEmpty()) {
            fail("High-Fidelity Canary Failure (LAW-034):$errorMessage")
        }
    }

    private fun isPathMatch(actualFile: File, expectedFile: File): Boolean {
        // Robust path matching: Compare by relative path from project root
        val actualRel = toRelative(actualFile).lowercase()
        val expectedRel = toRelative(expectedFile).lowercase()
        return actualRel == expectedRel
    }
    
    private fun toRelative(file: File): String {
        val path = file.absolutePath.replace("\\", "/")
        val root = projectRoot.replace("\\", "/")
        return if (path.startsWith(root, ignoreCase = true)) {
            path.substring(root.length).trimStart('/')
        } else {
            path
        }
    }

    private fun isLineMatch(actualLine: Int, expectedLine: Int): Boolean {
        return Math.abs(actualLine - expectedLine) <= 2
    }

    private fun isSeverityMatch(actual: String, expected: String?): Boolean {
        if (expected == null) return true
        val act = actual.uppercase()
        val exp = expected.uppercase()
        
        if (exp == "BLOCK" && act == "FATAL") return true
        if (exp == "FATAL" && act == "FATAL") return true
        if (exp == "ERROR" && act == "ERROR") return true
        if (exp == "WARN" && (act == "WARNING" || act == "WARN")) return true
        if (exp == "INFO" && (act == "INFORMATION" || act == "INFO")) return true
        
        return act == exp
    }

    private fun isMessageMatch(actual: String, token: String?): Boolean {
        if (token == null) return true
        return actual.contains(token, ignoreCase = true)
    }

    private fun collectExpectations(): List<CanaryExpectation> {
        val srcMain = File("$projectRoot/$canaryModulePath/src/main/kotlin")
        val srcTest = File("$projectRoot/$canaryModulePath/src/test/kotlin")
        
        val results = mutableListOf<CanaryExpectation>()
        
        listOf(srcMain, srcTest).filter { it.exists() }.forEach { root ->
            root.walkTopDown().filter { it.extension == "kt" }.forEach { file ->
                val relativePath = toRelative(file)
                file.readLines().forEachIndexed { index, line ->
                    val lineNumber = index + 1
                    
                    val posRegex = Regex("""\[CANARY:POSITIVE:([^:\]]+)(?::([^:\]]+))?(?::([^:\]]+))?\]""")
                    posRegex.findAll(line).forEach { match ->
                        results.add(CanaryExpectation(
                            issueId = match.groupValues[1],
                            line = lineNumber,
                            file = file,
                            relativePath = relativePath,
                            isPositive = true,
                            severity = match.groupValues[2].takeIf { it.isNotEmpty() },
                            messageToken = match.groupValues[3].takeIf { it.isNotEmpty() }
                        ))
                    }
                    
                    val negRegex = Regex("""\[CANARY:NEGATIVE:([^:\]]+)\]""")
                    negRegex.findAll(line).forEach { match ->
                        results.add(CanaryExpectation(
                            issueId = match.groupValues[1],
                            line = lineNumber,
                            file = file,
                            relativePath = relativePath,
                            isPositive = false
                        ))
                    }
                }
            }
        }
        return results
    }

    private data class ActualViolation(
        val issueId: String, 
        val line: Int, 
        val file: File, 
        val severity: String,
        val message: String
    )

    private fun parseActualViolations(xmlFile: File): List<ActualViolation> {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(xmlFile)
        val issues = doc.getElementsByTagName("issue")
        val results = mutableListOf<ActualViolation>()

        for (i in 0 until issues.length) {
            val element = issues.item(i) as Element
            val id = element.getAttribute("id")
            val severity = element.getAttribute("severity")
            val message = element.getAttribute("message")
            
            val locationElements = element.getElementsByTagName("location")
            if (locationElements.length > 0) {
                val location = locationElements.item(0) as Element
                val fileAttr = location.getAttribute("file")
                val lineAttr = location.getAttribute("line")
                
                if (lineAttr.isNotEmpty()) {
                    results.add(ActualViolation(id, lineAttr.toInt(), File(fileAttr), severity, message))
                }
            }
        }
        return results
    }
}
