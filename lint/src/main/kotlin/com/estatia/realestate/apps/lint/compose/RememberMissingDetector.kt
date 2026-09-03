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
 * Ensures that state-holding objects (like MutableState) are wrapped in 'remember' 
 * when used inside a Composable.
 */
class RememberMissingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("mutableStateOf", "mutableStateListOf", "mutableStateMapOf")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val containingMethod = node.getParentOfType<UMethod>()
        val isComposable = containingMethod?.javaPsi?.annotations?.any { it.qualifiedName?.contains("Composable") == true } == true

        if (isComposable) {
            val parentCall = node.uastParent as? UCallExpression
            val isRemembered = parentCall?.methodName?.startsWith("remember") == true

            if (!isRemembered) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "State creation '${node.methodName}' is not wrapped in 'remember'. This will cause state to be reset on every recomposition."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "RememberMissing",
            description = "State creation not wrapped in remember",
            rationale = "State must be cached across recompositions to maintain UI consistency.",
            badExample = "val count = mutableStateOf(0)",
            goodExample = "val count = remember { mutableStateOf(0) }",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-002 (Immutable State Boundaries)",
            implementation = Implementation(RememberMissingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
