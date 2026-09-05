package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*

/**
 * Ensures that state-holding objects (like MutableState) are wrapped in 'remember' 
 * when used inside a Composable.
 */
class RememberMissingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("mutableStateOf", "mutableStateListOf", "mutableStateMapOf")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!isMemberInPackage(method, "androidx.compose.runtime")) return
        
        val containingMethod = node.getParentOfType<UMethod>()
        val isComposable = containingMethod?.let { 
            context.evaluator.getAnnotations(it.javaPsi, false).any { ann -> ann.qualifiedName == "androidx.compose.runtime.Composable" }
        } ?: false

        if (isComposable) {
            var isRemembered = false
            var current: UElement? = node.uastParent
            while (current != null && current !is UMethod) {
                if (current is UCallExpression) {
                    val resolvedMethod = current.resolve()
                    if (resolvedMethod != null && isMemberInPackage(resolvedMethod, "androidx.compose.runtime") &&
                        resolvedMethod.name.startsWith("remember")) {
                        isRemembered = true
                        break
                    }
                }
                current = current.uastParent
            }

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

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: return false
        return qualifiedName.startsWith("$packageName.")
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
