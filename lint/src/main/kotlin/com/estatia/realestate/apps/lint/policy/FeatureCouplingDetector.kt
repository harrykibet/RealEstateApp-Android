package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import org.jetbrains.uast.UImportStatement

/**
 * LAW-004: Feature module isolation.
 * Prevents feature modules from depending on other feature modules directly.
 */
class FeatureCouplingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UImportStatement::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitImportStatement(node: UImportStatement) {
            val importPath = node.importReference?.asRenderString() ?: return
            val currentPackage = context.uastFile?.packageName ?: ""

            if (currentPackage.contains(".feature.")) {
                val currentFeature = getFeatureName(currentPackage)
                if (importPath.contains(".feature.") && 
                    ArchitecturalPolicy.Law004.AllowedCouplingPackages.none { importPath.contains(it) }) {
                    
                    val importedFeature = getFeatureName(importPath)
                    if (currentFeature != null && importedFeature != null && currentFeature != importedFeature) {
                        context.report(
                            ISSUE,
                            node,
                            context.getLocation(node),
                            "Feature module '$currentFeature' cannot depend on feature '$importedFeature' (LAW-004)."
                        )
                    }
                }
            }
        }
    }

    private fun getFeatureName(packageName: String): String? {
        val regex = Regex("\\.feature\\.([^.]+)")
        return regex.find(packageName)?.groupValues?.get(1)
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "FeatureCouplingViolation",
            description = "Unauthorized feature module coupling",
            rationale = "Feature modules must remain isolated to ensure scalability and build speed.",
            badExample = "import com.estatia.realestate.apps.feature.auth.AuthRepo // In feature.home",
            goodExample = "import com.estatia.realestate.apps.core.domain.security.IAuthRepository",
            category = IssueCategory.ARCHITECTURE,
            architectureLaw = Law.LAW_004,
            implementation = Implementation(FeatureCouplingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
