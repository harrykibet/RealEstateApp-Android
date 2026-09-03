package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression
import com.intellij.psi.PsiClass

/**
 * Enforces the Estatia Module Dependency Policy.
 * Prevents illegal architectural coupling (e.g., Feature-to-Feature dependencies)
 * and enforces strict layering.
 */
class ModuleDependencyDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UCallExpression::class.java, UQualifiedReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitCallExpression(node: UCallExpression) {
            checkDependency(context, node, node.resolve()?.containingClass)
        }

        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            val resolved = node.resolve()
            if (resolved is PsiClass) {
                checkDependency(context, node, resolved)
            }
        }
    }

    private fun checkDependency(context: JavaContext, node: UElement, targetClass: PsiClass?) {
        val targetPackage = context.evaluator.getPackage(targetClass ?: return)?.qualifiedName ?: return
        
        // We use context.file.path or evaluator to find the source package
        val currentPackage = context.evaluator.getPackage(context.uastFile!!)?.qualifiedName ?: return
        
        // 1. Feature to Feature Coupling (LAW-004)
        if (currentPackage.startsWith("com.estatia.realestate.apps.feature.")) {
            val currentFeature = getModuleName(currentPackage)
            
            if (targetPackage.startsWith("com.estatia.realestate.apps.feature.")) {
                val targetFeature = getModuleName(targetPackage)
                
                // Allow self-dependency and shared_ui exception
                if (currentFeature != targetFeature && targetFeature != "shared_ui" && currentFeature.isNotEmpty()) {
                    context.report(
                        FEATURE_COUPLING_ISSUE,
                        node,
                        context.getLocation(node),
                        "Illegal Feature Coupling: Feature '$currentFeature' cannot depend on Feature '$targetFeature'. Use a shared core module or navigation events instead."
                    )
                }
            }
        }

        // 2. Implementation Leakage (Database/Firebase into Feature)
        if (currentPackage.startsWith("com.estatia.realestate.apps.feature.")) {
            if (targetPackage.startsWith("com.estatia.realestate.apps.core.database") || 
                targetPackage.startsWith("com.google.firebase") ||
                targetPackage.startsWith("com.amplifyframework")) {
                
                context.report(
                    IMPLEMENTATION_LEAKAGE_ISSUE,
                    node,
                    context.getLocation(node),
                    "Illegal Implementation Dependency: Features must depend on 'core:domain' or 'core:model' abstractions, not database or external SDK implementations."
                )
            }
        }
    }

    private fun getModuleName(pkg: String): String {
        val parts = pkg.split(".")
        return if (parts.size > 5) parts[5] else ""
    }

    companion object {
        val FEATURE_COUPLING_ISSUE = EstatiaIssue.create(
            id = "FeatureCouplingViolation",
            description = "Feature-to-Feature dependency detected",
            explanation = """
                To maintain scalability and allow for dynamic delivery, features must be 
                isolated. They cannot depend on each other directly. Shared logic must 
                be extracted to a core module.
            """,
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(ModuleDependencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val IMPLEMENTATION_LEAKAGE_ISSUE = EstatiaIssue.create(
            id = "LayerViolation",
            description = "Feature depends on implementation detail",
            explanation = """
                Features must interact with the Data layer through Domain abstractions. 
                Direct dependencies on Database (Room) or SDKs (Firebase/AWS) are forbidden 
                to ensure the UI remains testable and agnostic of infrastructure.
            """,
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(ModuleDependencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
        
        val ISSUE = FEATURE_COUPLING_ISSUE 
    }
}
