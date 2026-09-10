package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.detector.api.TextFormat
import com.estatia.realestate.apps.core.architecture.Enforcement
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.registry.EstatiaIssueRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * LAW-040: Baseline Monotonicity.
 * LAW-035: Fatal Baseline Integrity.
 * 
 * authoritatively enforces that architectural health never regresses 
 * by ensuring the baseline file strictly decreases or remains stable.
 */
class BaselineMonotonicityTest {

    private val rootDir = File("C:/Users/Administrator/StudioProjects/RealEstateApp-Android")

    data class BaselineSummary(
        val blockCount: Int,
        val warnCount: Int,
        val infoCount: Int,
        val totalCount: Int,
        val architecturalIssues: List<String>
    )

    @Test
    fun `baseline must follow the Monotonicity Ratchet policy`() {
        val baselineFile = File(rootDir, "lint-baseline.xml")
        
        if (!baselineFile.exists()) {
            println("INFO: No baseline found. System is in Pure State.")
            return
        }

        val currentSummary = analyzeBaseline(baselineFile)

        // 1. LAW-035: BLOCK (FATAL) issues must ALWAYS be zero in baseline
        assertTrue(
            "LAW-035 Violation: Baseline contains ${currentSummary.blockCount} BLOCK-level violations. " +
            "FATAL architectural rules must NEVER be baselined. Violations:\n" +
            currentSummary.architecturalIssues.filter { it.contains("BLOCK") }.joinToString("\n"),
            currentSummary.blockCount == 0
        )

        // 2. LAW-040: Check against reference baseline if provided (Monotonicity)
        val referencePath = System.getProperty("MAIN_BASELINE_PATH")
        if (referencePath != null) {
            val referenceFile = File(referencePath)
            if (referenceFile.exists()) {
                val refSummary = analyzeBaseline(referenceFile)
                
                assertTrue(
                    "LAW-040 Violation: Architectural ERROR/WARN baseline count increased from " +
                    "${refSummary.warnCount} to ${currentSummary.warnCount}.",
                    currentSummary.warnCount <= refSummary.warnCount
                )
                
                assertTrue(
                    "LAW-040 Violation: Total lint baseline count increased from " +
                    "${refSummary.totalCount} to ${currentSummary.totalCount}.",
                    currentSummary.totalCount <= refSummary.totalCount
                )
            }
        }
        
        println("Architectural Baseline Summary:")
        println("  - BLOCK: ${currentSummary.blockCount}")
        println("  - WARN/ERROR: ${currentSummary.warnCount}")
        println("  - INFO: ${currentSummary.infoCount}")
        println("  - Total Issues: ${currentSummary.totalCount}")
    }

    private fun analyzeBaseline(file: File): BaselineSummary {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(file)
        val issues = doc.getElementsByTagName("issue")
        
        var blocks = 0
        var warns = 0
        var infos = 0
        val archViolations = mutableListOf<String>()
        
        val registry = EstatiaIssueRegistry()
        val issueMap = registry.issues.associate { issue ->
            val explanation = issue.getExplanation(TextFormat.TEXT)
            val lawMatch = Regex("""Architecture Law: (LAW-\d+)""").find(explanation)
            val lawId = lawMatch?.groupValues?.get(1)
            val law = Law.entries.find { it.id == lawId }
            issue.id to law
        }

        for (i in 0 until issues.length) {
            val element = issues.item(i) as Element
            val id = element.getAttribute("id")
            val message = element.getAttribute("message")
            
            val law = issueMap[id]
            
            if (law != null) {
                when (law.enforcement) {
                    Enforcement.BLOCK -> {
                        blocks++
                        archViolations.add("BLOCK: $id ($message)")
                    }
                    Enforcement.WARN -> warns++
                    Enforcement.INFO -> infos++
                }
            } else {
                // Standard Android/Third-party lint issues
                // We track them as total count for LAW-040 total monotonicity
            }
        }
        
        return BaselineSummary(blocks, warns, infos, issues.length, archViolations)
    }
}
