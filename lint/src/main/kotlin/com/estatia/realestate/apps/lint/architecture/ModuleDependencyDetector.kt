package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Enforces the Estatia Module Dependency Policy.
 */
class ModuleDependencyDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UCallExpression::class.java, UImportStatement::class.java, UQualifiedReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitImportStatement(node: UImportStatement) {
            checkPath(context, node, node.importReference?.asRenderString() ?: "")
        }

        override fun visitCallExpression(node: UCallExpression) {
            checkPath(context, node, node.asRenderString())
        }
        
        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            checkPath(context, node, node.asRenderString())
        }
    }

    private fun checkPath(context: JavaContext, node: UElement, targetText: String) {
        val path = context.file.path.replace("\\", "/")
        if (!path.contains("/feature/")) return
        
        val parts = path.split("/feature/")
        val currentFeature = parts.getOrNull(1)?.split("/")?.firstOrNull() ?: ""

        if (targetText.contains(".feature.")) {
            val targetFeature = targetText.split(".feature.").getOrNull(1)?.split(".")?.firstOrNull() ?: ""
            
            if (targetFeature.isNotEmpty() && currentFeature != targetFeature && targetFeature != "shared_ui" && targetFeature != "navigation") {
                context.report(
                    FEATURE_COUPLING_ISSUE,
                    node,
                    context.getLocation(node),
                    "Illegal Feature Coupling: Feature '$currentFeature' cannot depend on Feature '$targetFeature' (LAW-004)."
                )
            }
        }

        if (targetText.contains(".core.database") || targetText.contains(".core.network") || targetText.contains("firebase")) {
            context.report(
                IMPLEMENTATION_LEAKAGE_ISSUE,
                node,
                context.getLocation(node),
                "Illegal Implementation Dependency: Feature '$currentFeature' cannot depend on implementation '$targetText' (LAW-003)."
            )
        }
    }

    companion object {
        val FEATURE_COUPLING_ISSUE = EstatiaIssue.create(
            id = "FeatureCouplingViolation",
            description = "Feature-to-Feature dependency detected",
            rationale = "Features must be isolated. Use a core module or navigation events.",
            badExample = "// In :feature:profile\nimport com.estatia.feature.home.HomeActivity",
            goodExample = "// In :feature:profile\nimport com.estatia.core.navigation.HomeNavigator",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-004",
            implementation = Implementation(ModuleDependencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val IMPLEMENTATION_LEAKAGE_ISSUE = EstatiaIssue.create(
            id = "LayerViolation",
            description = "Feature depends on implementation detail",
            rationale = "Features must interact with the Data layer through Domain abstractions.",
            badExample = "class UserViewModel(val db: RoomDatabase)",
            goodExample = "class UserViewModel(val repository: UserRepository)",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-003",
            implementation = Implementation(ModuleDependencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
        
        val ISSUE = FEATURE_COUPLING_ISSUE 
    }
}
