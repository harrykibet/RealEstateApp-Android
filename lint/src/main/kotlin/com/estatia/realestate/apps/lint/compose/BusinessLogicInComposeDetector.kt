package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.getParentOfType

/**
 * Detects complex business logic (e.g., repository calls) inside Composable functions.
 */
class BusinessLogicInComposeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("launch", "async", "collect", "execute")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val containingMethod = node.getParentOfType<UMethod>()
        val hasComposableAnnotation = containingMethod?.let { 
            context.evaluator.getAnnotations(it.javaPsi, false).any { ann -> ann.qualifiedName == "androidx.compose.runtime.Composable" }
        } ?: false

        if (hasComposableAnnotation) {
            val methodName = node.methodName ?: return
            
            val isForbidden = when (methodName) {
                "launch" -> isMemberInPackage(method, "kotlinx.coroutines")
                "collect" -> isMemberInPackage(method, "kotlinx.coroutines.flow")
                else -> false
            }

            if (isForbidden) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Avoid complex logic or side-effects like '$methodName' directly in a Composable. Move this logic to a ViewModel."
                )
            }
        }
    }

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: return false
        return qualifiedName.startsWith("$packageName.") || qualifiedName == packageName
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "BusinessLogicInCompose",
            description = "Business logic detected in Composable",
            rationale = "Composables should be pure UI projections to remain testable.",
            badExample = "@Composable fun List() { repo.load().collect { ... } }",
            goodExample = "@Composable fun List(state: State) { ... }",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-001 (Presentation Purity)",
            implementation = Implementation(BusinessLogicInComposeDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
