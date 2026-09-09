package com.estatia.realestate.apps.lint.doc

import com.android.tools.lint.client.api.LintClient
import com.estatia.realestate.apps.core.architecture.Confidence
import com.estatia.realestate.apps.core.architecture.Enforcement
import com.estatia.realestate.apps.core.architecture.Law
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Global Governance Test.
 * 
 * authoritatively enforces that all enforcement layers (Lint, KSP, Konsist) 
 * are correctly documented and registered in the system.
 */
class MetaArchitectureSyncTest {

    @Before
    fun setUp() {
        try {
            LintClient.clientName = "EstatiaTest"
        } catch (_: Exception) { }
    }

    private fun getFileFromProperty(prop: String): File {
        val path = System.getProperty(prop)
        assertNotNull("Governance Violation: System property '$prop' is not set.", path)
        return File(path!!)
    }

    @Test
    fun `KSP rules in README must be registered in META-INF services`() {
        val readmeFile = getFileFromProperty("LINT_README_PATH")
        val kspServicesFile = getFileFromProperty("KSP_SERVICES_PATH")
        
        val readmeContent = readmeFile.readText()
        val servicesContent = kspServicesFile.readText()

        // Match: `K:ProcessorName`
        val kspRuleRegex = Regex("""`K:([^`]+)`""")
        val rulesInReadme = kspRuleRegex.findAll(readmeContent).map { it.groupValues[1] }.toSet()

        rulesInReadme.forEach { processor ->
            assertTrue(
                "Governance Violation: KSP Processor '$processor' is mentioned in README.md but NOT registered in $kspServicesFile. " +
                "KSP rules must be registered in the service provider list to actually run.",
                servicesContent.contains(processor)
            )
        }
    }

    @Test
    fun `Konsist rules in README must have corresponding test implementation`() {
        val readmeFile = getFileFromProperty("LINT_README_PATH")
        val konsistTestDir = getFileFromProperty("KONSIST_TESTS_PATH")
        
        val readmeContent = readmeFile.readText()
        val testFiles = konsistTestDir.listFiles()?.map { it.name } ?: emptyList()

        // Match: `S:TestName`
        val konsistRuleRegex = Regex("""`S:([^`]+)`""")
        val rulesInReadme = konsistRuleRegex.findAll(readmeContent).map { it.groupValues[1] }.toSet()

        rulesInReadme.forEach { testName ->
            val expectedFileName = "$testName.kt"
            assertTrue(
                "Governance Violation: Konsist Rule '$testName' is mentioned in README.md but NO test file was found at $konsistTestDir/$expectedFileName. " +
                "Global structural rules must have a corresponding test implementation.",
                testFiles.contains(expectedFileName)
            )
        }
    }

    @Test
    fun `every Law ID in the enum must have at least one documented enforcement rule and matching metadata`() {
        val readmeFile = getFileFromProperty("LINT_README_PATH")
        val readmeContent = readmeFile.readText()

        Law.entries.forEach { law ->
            // Skip conventions which might be project-wide guidelines
            if (law.enforcement == Enforcement.INFO) return@forEach

            val rowRegex = Regex("""\| \*\*${law.id}\*\* \| [^|]+ \| `([^`]+)` \| `([^`]+)` \| `([^`]+)` \| ([^|]+) \|""")
            val match = rowRegex.find(readmeContent)
            
            assertNotNull("Governance Violation: Law '${law.id}' is missing from the README table.", match)
            
            val readmeRisk = match!!.groupValues[1].trim()
            val readmeConfidence = match.groupValues[2].trim()
            val readmeEnforcement = match.groupValues[3].trim()
            val enforcementColumn = match.groupValues[4]
            
            // 1. Verify Risk sync
            assertTrue(
                "Governance Violation: Law '${law.id}' risk mismatch. Enum: ${law.risk}, README: $readmeRisk",
                law.risk.name == readmeRisk
            )

            // 2. Verify Confidence sync
            assertTrue(
                "Governance Violation: Law '${law.id}' confidence mismatch. Enum: ${law.confidence}, README: $readmeConfidence",
                law.confidence.name == readmeConfidence
            )

            // 3. Verify Enforcement sync
            assertTrue(
                "Governance Violation: Law '${law.id}' enforcement mismatch. Enum: ${law.enforcement}, README: $readmeEnforcement",
                law.enforcement.name == readmeEnforcement
            )

            // 4. Verify Enforcement exists
            assertTrue(
                "Governance Violation: Law '${law.id}' has no enforcement rules (L:, K:, S:, T:, V:, H:) listed in README.md.",
                enforcementColumn.contains(":")
            )
        }
    }

    @Test
    fun `BLOCK laws must be backed by high-confidence enforcement`() {
        Law.entries.filter { it.enforcement == Enforcement.BLOCK }.forEach { law ->
            assertTrue(
                "Governance Violation: BLOCK Law '${law.id}' is enforced via HEURISTIC methods. " +
                "BLOCK rules MUST use CERTAIN (Symbol resolution) or HIGH (Topology) enforcement.",
                law.confidence != Confidence.HEURISTIC
            )
        }
    }
}
