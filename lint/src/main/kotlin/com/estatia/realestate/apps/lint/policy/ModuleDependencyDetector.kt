package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import com.intellij.psi.PsiClass
import org.jetbrains.uast.UFile
import org.jetbrains.uast.UImportStatement

/**
 * Enforces LAW-003 and LAW-004: Module Isolation and Abstraction Boundaries.
 */
class ModuleDependencyDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UImportStatement::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitImportStatement(node: UImportStatement) {
            val importPath = node.importReference?.asRenderString() ?: return
            val currentPackage = context.uastFile?.packageName ?: ""

            // 🏎️ CRASH RESILIENCE: We check the import path string directly 
            // even if resolve() would return null (common in complex builds).

            // 1. LAW-004: Feature Coupling
            if (currentPackage.contains(".feature.")) {
                val currentFeature = getFeatureName(currentPackage)
                if (importPath.contains(".feature.") && 
                    !importPath.contains(".shared_ui") && 
                    !importPath.contains(".navigation") &&
                    !importPath.contains(".core.ui")) { // Allow core.ui interop if needed
                    
                    val importedFeature = getFeatureName(importPath)
                    if (currentFeature != null && importedFeature != null && currentFeature != importedFeature) {
                        context.report(
                            FEATURE_COUPLING_ISSUE,
                            node,
                            context.getLocation(node),
                            "Feature module '$currentFeature' cannot depend on feature '$importedFeature' (LAW-004)."
                        )
                    }
                }
            }

            // 2. LAW-003: Infrastructure Leakage
            if (currentPackage.contains(".domain") || currentPackage.contains(".model")) {
                if (isInfrastructure(importPath)) {
                    context.report(
                        IMPLEMENTATION_LEAKAGE_ISSUE,
                        node,
                        context.getLocation(node),
                        "Infrastructure implementation '$importPath' leaked into pure layer (LAW-003)."
                    )
                }
            }
        }
    }

    private fun getFeatureName(packageName: String): String? {
        val regex = Regex("\\.feature\\.([^.]+)")
        return regex.find(packageName)?.groupValues?.get(1)
    }

    private fun isInfrastructure(path: String): Boolean {
        return ArchitecturalPolicy.InfrastructurePackages.any { path.contains(it) }
    }

    companion object {
        val FEATURE_COUPLING_ISSUE = EstatiaIssue.create(
            id = "FeatureCouplingViolation",
            description = "Unauthorized feature module coupling",
            rationale = "Feature modules must remain isolated to ensure scalability and build speed.",
            badExample = "import com.estatia.realestate.apps.feature.auth.AuthRepo // In feature.home",
            goodExample = "import com.estatia.realestate.apps.core.domain.security.IAuthRepository",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_004,
            implementation = Implementation(ModuleDependencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val IMPLEMENTATION_LEAKAGE_ISSUE = EstatiaIssue.create(
            id = "InfrastructureLeakage",
            description = "Implementation detail leaked into pure layer",
            rationale = "Domain and Model layers must remain pure Kotlin and decoupled from infrastructure.",
            badExample = "import androidx.room.Entity // In core.domain",
            goodExample = "import com.estatia.realestate.apps.core.model.User",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_003,
            implementation = Implementation(ModuleDependencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
