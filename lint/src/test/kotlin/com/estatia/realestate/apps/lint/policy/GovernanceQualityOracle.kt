package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.detector.api.TextFormat
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.Enforcement
import com.estatia.realestate.apps.lint.registry.EstatiaPolicyGroups
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Global Governance Quality Metric Engine.
 * 
 * authoritatively measures the Precision and Recall of architectural laws 
 * by correlating canary specimens with actual reported violations.
 */
class GovernanceQualityOracle {

    private val projectRoot = System.getProperty("PROJECT_ROOT") ?: "C:/Users/Administrator/StudioProjects/RealEstateApp-Android"
    private val canaryModulePath = "core/canary-violations"

    data class LawMetrics(
        val lawId: String,
        val truePositives: Int = 0,
        val falsePositives: Int = 0,
        val falseNegatives: Int = 0,
        val t1Count: Int = 0,
        val t2Count: Int = 0,
        val t3Count: Int = 0
    ) {
        val precision: Double get() = if (truePositives + falsePositives == 0) 1.0 else truePositives.toDouble() / (truePositives + falsePositives)
        val recall: Double get() = if (truePositives + falseNegatives == 0) 1.0 else truePositives.toDouble() / (truePositives + falseNegatives)
        
        val robustness: String get() = when {
            t3Count > 0 && falseNegatives == 0 -> "HARDENED (T3)"
            t2Count > 0 && falseNegatives == 0 -> "SEMANTIC (T2)"
            t1Count > 0 && falseNegatives == 0 -> "SYNTACTIC (T1)"
            else -> "UNVERIFIED"
        }
    }

    @Test
    fun `verify governance quality metrics`() {
        val reportPath = "$projectRoot/$canaryModulePath/build/reports/lint-results.xml"
        val reportFile = File(reportPath)
        assertTrue("Governance Quality Error: Canary lint report not found at $reportPath. Run :core:canary-violations:lintAnalyzeDemoDebug first.", reportFile.exists())

        val actualViolations = parseActualViolations(reportFile)
        val expectations = collectExpectations()
        
        val metricsMap = mutableMapOf<String, LawMetrics>()

        Law.entries.forEach { law ->
            var tp = 0
            var fp = 0
            var fn = 0
            var t1 = 0
            var t2 = 0
            var t3 = 0

            // 1. Calculate TP and FN (Based on positive expectations)
            val positiveExp = expectations.filter { it.isPositive && it.lawId == law.id }
            positiveExp.forEach { exp ->
                when (exp.tier) {
                    1 -> t1++
                    2 -> t2++
                    3 -> t3++
                }

                val wasDetected = actualViolations.any { act -> 
                    act.issueId == exp.issueId && 
                    isPathMatch(act.file, exp.file) &&
                    isLineMatch(act.line, exp.line)
                }
                if (wasDetected) {
                    tp++
                } else {
                    fn++
                    println("DEBUG: FN for ${law.id} [Tier: T${exp.tier}] [Issue: ${exp.issueId}] at ${exp.file.name}:${exp.line}")
                }
            }

            // 2. Calculate FP (Based on negative expectations)
            val negativeExp = expectations.filter { !it.isPositive && it.lawId == law.id }
            negativeExp.forEach { exp ->
                val wasIncorrectlyDetected = actualViolations.any { act -> 
                    act.issueId == exp.issueId && 
                    isPathMatch(act.file, exp.file) &&
                    isLineMatch(act.line, exp.line)
                }
                if (wasIncorrectlyDetected) {
                    fp++
                    println("DEBUG: FP for ${law.id} [Issue: ${exp.issueId}] at ${exp.file.name}:${exp.line}")
                }
            }
            
            metricsMap[law.id] = LawMetrics(law.id, tp, fp, fn, t1, t2, t3)
        }

        generateReport(metricsMap.values.toList())

        // 🛡️ REFINEMENT: Industrial Quality Floor Enforcement
        val failures = metricsMap.values.filter { it.precision < Law.valueOf(it.lawId.replace("-", "_")).targetPrecision || 
                                                 it.recall < Law.valueOf(it.lawId.replace("-", "_")).targetRecall }
        
        if (failures.isNotEmpty()) {
            val msg = failures.joinToString("\n") { 
                val law = Law.valueOf(it.lawId.replace("-", "_"))
                "  - ${it.lawId}: Precision=${(it.precision*100).toInt()}% (Target: ${(law.targetPrecision*100).toInt()}%), Recall=${(it.recall*100).toInt()}% (Target: ${(law.targetRecall*100).toInt()}%)"
            }
            error("Governance Quality Floor Violated (Regression in Detector Precision/Recall):\n$msg")
        }
    }

    private fun isPathMatch(actualFile: File, expectedFile: File): Boolean {
        return toRelative(actualFile).lowercase() == toRelative(expectedFile).lowercase()
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

    private fun isLineMatch(actualLine: Int, expectedLine: Int): Boolean = Math.abs(actualLine - expectedLine) <= 2

    private fun parseActualViolations(xmlFile: File): List<ActualViolation> {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(xmlFile)
        val issues = doc.getElementsByTagName("issue")
        val results = mutableListOf<ActualViolation>()

        for (i in 0 until issues.length) {
            val element = issues.item(i) as Element
            val id = element.getAttribute("id")
            val locationElements = element.getElementsByTagName("location")
            if (locationElements.length > 0) {
                val location = locationElements.item(0) as Element
                val fileAttr = location.getAttribute("file")
                val lineAttr = location.getAttribute("line")
                if (lineAttr.isNotEmpty()) {
                    results.add(ActualViolation(id, lineAttr.toInt(), File(fileAttr)))
                }
            }
        }
        return results
    }

    private fun collectExpectations(): List<CanaryExpectation> {
        val results = mutableListOf<CanaryExpectation>()
        val srcMain = File("$projectRoot/$canaryModulePath/src/main/kotlin")
        
        srcMain.walkTopDown().filter { it.extension == "kt" }.forEach { file ->
            val tier = when {
                file.name.contains("Tier1") -> 1
                file.name.contains("Tier2") -> 2
                file.name.contains("Tier3") -> 3
                else -> 1 // Default to Syntactic for legacy canaries
            }

            file.readLines().forEachIndexed { index, line ->
                val lineNumber = index + 1
                val posRegex = Regex("""\[CANARY:POSITIVE:([^:\]]+)""")
                posRegex.findAll(line).forEach { match ->
                    val issueId = match.groupValues[1]
                    val law = findLawForIssueId(issueId)
                    if (law != null) {
                        results.add(CanaryExpectation(law.id, issueId, lineNumber, file, true, tier))
                    }
                }
                val negRegex = Regex("""\[CANARY:NEGATIVE:([^:\]]+)""")
                negRegex.findAll(line).forEach { match ->
                    val issueId = match.groupValues[1]
                    val law = findLawForIssueId(issueId)
                    if (law != null) {
                        results.add(CanaryExpectation(law.id, issueId, lineNumber, file, false, tier))
                    }
                }
            }
        }
        return results
    }

    private fun findLawForIssueId(issueId: String): Law? {
        val issue = EstatiaPolicyGroups.all.find { it.id == issueId }
        return if (issue != null) {
            val explanation = issue.getExplanation(TextFormat.RAW)
            Law.entries.find { explanation.contains(it.id) }
        } else {
            Law.entries.find { it.id == issueId }
        }
    }

    private fun generateReport(metrics: List<LawMetrics>) {
        val report = StringBuilder("# Estatia — Governance Integrity Report\n\n")
        report.append("This report authoritatively measures the **Precision**, **Recall**, and **Robustness** of Estatia's architectural enforcement system.\n\n")
        
        report.append("## Summary Statistics\n")
        val totalLaws = metrics.size
        val hardenedLaws = metrics.count { it.robustness.startsWith("HARDENED") }
        val failedRecall = metrics.count { it.recall < 1.0 && Law.valueOf(it.lawId.replace("-", "_")).enforcement == Enforcement.BLOCK }
        
        report.append("- **Total Laws Governed**: $totalLaws\n")
        report.append("- **Hardened Laws (Tier 3 Verified)**: $hardenedLaws\n")
        report.append("- **Industrial Recall Failures**: ${if (failedRecall == 0) "✅ 0" else "❌ $failedRecall"}\n\n")

        report.append("## Detailed Quality Matrix\n\n")
        report.append("| Law ID | Status | Precision | Recall | Robustness | TP | FP | FN | Specimens |\n")
        report.append("| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :--- | :--- |\n")
        
        metrics.sortedBy { it.lawId }.forEach { m ->
            val law = Law.valueOf(m.lawId.replace("-", "_"))
            val status = when {
                m.recall < law.targetRecall -> "❌"
                m.precision < law.targetPrecision -> "⚠️"
                else -> "✅"
            }
            val specimens = "T1:${m.t1Count}, T2:${m.t2Count}, T3:${m.t3Count}"
            report.append("| **${m.lawId}** | $status | ${(m.precision * 100).toInt()}% | ${(m.recall * 100).toInt()}% | ${m.robustness} | ${m.truePositives} | ${m.falsePositives} | ${m.falseNegatives} | $specimens |\n")
        }

        report.append("\n---\n*Report generated automatically by `GovernanceQualityOracle`*")

        // Authoritative Doc Output
        val docsDir = File("$projectRoot/docs")
        if (docsDir.exists()) {
            File(docsDir, "GOVERNANCE_INTEGRITY_REPORT.md").writeText(report.toString())
        }

        // IDE Artifact Output
        val artifactDir = File("$projectRoot/.artifacts/b2fbff85-3b1f-49b1-9c69-a8b322c659ee")
        if (artifactDir.exists()) {
            File(artifactDir, "governance_quality_report.artifact.md").writeText(report.toString())
        }
    }

    private data class ActualViolation(val issueId: String, val line: Int, val file: File)
    private data class CanaryExpectation(val lawId: String, val issueId: String, val line: Int, val file: File, val isPositive: Boolean, val tier: Int)
}
