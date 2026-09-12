package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.LawEnforcer
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

/**
 * LAW-034: High-Fidelity KSP Canary Regression.
 * 
 * authoritatively verifies that KSP processors detect the EXACT code constructs 
 * they are intended for in our authoritative canary specimens.
 */
@OptIn(ExperimentalCompilerApi::class)
class Law034_KspCanaryRegressionTest {

    private val projectRoot = "C:/Users/Administrator/StudioProjects/RealEstateApp-Android"
    private val canaryPath = "$projectRoot/core/canary-violations/src/main/kotlin"

    data class KspExpectation(
        val issueId: String,
        val line: Int,
        val file: File,
        val isPositive: Boolean
    )

    @Test
    fun `KSP processors must satisfy the High-Fidelity Enforcement Oracle`() {
        val providers = listOf(
            Law009_ResultWrappingProcessorProvider(),
            Law036_DomainExpressivenessProcessorProvider(),
            Law030_ConstructorPurityProcessorProvider(),
            Law018_ViewModelSsotProcessorProvider(),
            Law002_ExposedMutableStateProcessorProvider(),
            Law041_IdentityIntegrityProcessorProvider()
        )

        // 1. Collect all canary files
        val canaryFiles = File(canaryPath).walkTopDown()
            .filter { it.extension == "kt" }
            .toList()

        val sourceFiles = canaryFiles.map { SourceFile.fromPath(it) }

        // 2. Compile everything with KSP
        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource,
            KspTestUtils.resultSource,
            KspTestUtils.coroutineStubs,
            KspTestUtils.lifecycleStubs,
            *sourceFiles.toTypedArray(),
            providers = providers
        )

        // 3. Collect expectations from source markers
        val expectations = collectExpectations(canaryFiles)
            .filter { exp -> 
                // Only check laws enforced by KSP
                val law = Law.entries.find { it.id == exp.issueId }
                law?.enforcers?.contains(LawEnforcer.KSP) == true
            }

        val actualViolations = parseActualViolations(result.messages)

        // 4. Verify Positive Canaries (Regression detected if missing)
        val missingPositives = expectations.filter { it.isPositive }.filterNot { exp ->
            val matches = actualViolations.filter { act ->
                act.lawId == exp.issueId &&
                act.file.name == exp.file.name &&
                isLineMatch(act.line, exp.line, exp.issueId)
            }
            
            if (matches.isEmpty() && exp.issueId != "LintCanaryActive") {
                println("DEBUG: Failed to match Positive KSP Canary [${exp.issueId}] at ${exp.file.name}:${exp.line}")
                actualViolations.filter { it.lawId == exp.issueId && it.file.name == exp.file.name }.forEach { act ->
                    println("    Candidate at different line: ${act.line}")
                }
            }
            
            matches.isNotEmpty()
        }

        // 5. Verify Negative Canaries (False positive detected if present)
        val falsePositives = expectations.filter { !it.isPositive }.filter { exp ->
            actualViolations.any { act ->
                act.lawId == exp.issueId &&
                act.file.name == exp.file.name &&
                isLineMatch(act.line, exp.line, exp.issueId)
            }
        }

        // 6. Build Error Message
        val errorMessage = StringBuilder()
        if (missingPositives.isNotEmpty()) {
            errorMessage.append("\n❌ MISSING POSITIVE KSP CANARIES (Regression detected):\n")
            missingPositives.forEach { 
                errorMessage.append("  - ${it.issueId} at ${it.file.name}:${it.line}\n") 
            }
        }

        if (falsePositives.isNotEmpty()) {
            errorMessage.append("\n❌ FAILED NEGATIVE KSP CANARIES (False positive detected):\n")
            falsePositives.forEach { 
                errorMessage.append("  - ${it.issueId} incorrectly flagged at ${it.file.name}:${it.line}\n") 
            }
        }

        if (errorMessage.isNotEmpty()) {
            fail("High-Fidelity KSP Canary Failure (LAW-034):$errorMessage\nCompiler Messages:\n${result.messages}")
        }
    }

    private fun isLineMatch(actualLine: Int, expectedLine: Int, lawId: String): Boolean {
        // Class-level laws might be reported at the class header (line 14-16) 
        // while the marker is inside the class (line 20).
        val window = if (lawId == "LAW-018" || lawId == "LAW-029" || lawId == "LAW-041") 10 else 2
        return Math.abs(actualLine - expectedLine) <= window
    }

    private fun collectExpectations(files: List<File>): List<KspExpectation> {
        val results = mutableListOf<KspExpectation>()
        files.forEach { file ->
            file.readLines().forEachIndexed { index, line ->
                val lineNumber = index + 1
                
                // Match: [CANARY:POSITIVE:IssueId]
                // Note: KSP issue IDs often match the Law ID (e.g. LAW-009)
                Regex("""\[CANARY:POSITIVE:([^:\]]+)\]""").findAll(line).forEach { match ->
                    val token = match.groupValues[1]
                    // If token is e.g. "ViewModelSsot", map to "LAW-018"
                    val lawId = mapTokenToLawId(token)
                    results.add(KspExpectation(lawId, lineNumber, file, true))
                }
                
                Regex("""\[CANARY:NEGATIVE:([^:\]]+)\]""").findAll(line).forEach { match ->
                    val lawId = mapTokenToLawId(match.groupValues[1])
                    results.add(KspExpectation(lawId, lineNumber, file, false))
                }
            }
        }
        return results
    }

    private fun mapTokenToLawId(token: String): String {
        return when (token) {
            "ViewModelSsot" -> "LAW-018"
            "ExposedMutableState" -> "LAW-002"
            "ImplementationTypeInPublicApi" -> "LAW-008"
            "MissingResultWrapper" -> "LAW-009"
            "DomainExpressiveness" -> "LAW-036"
            "ConstructorAbstractions" -> "LAW-030"
            "IdentityMandate" -> "LAW-041"
            "IdentityIntegrity" -> "LAW-041"
            else -> token // Assume it's already a law ID
        }
    }

    private data class ActualViolation(val lawId: String, val line: Int, val file: File)

    private fun parseActualViolations(messages: String): List<ActualViolation> {
        val results = mutableListOf<ActualViolation>()
        // Format: [ksp] <path>:<line>: FATAL Architecture Law: LAW-009 ...
        val regex = Regex("""\[ksp\] (.*?):(\d+):\s+(?:FATAL|ERROR|WARNING) Architecture Law: (LAW-\d+)""")
        regex.findAll(messages).forEach { match ->
            results.add(ActualViolation(
                lawId = match.groupValues[3],
                line = match.groupValues[2].toInt(),
                file = File(match.groupValues[1])
            ))
        }
        return results
    }
}
