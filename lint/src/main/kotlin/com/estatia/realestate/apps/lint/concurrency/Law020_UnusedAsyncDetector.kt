package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.PsiMethod

/**
 * LAW-020: Explicit Join.
 * async calls where the Deferred result is ignored.
 */
class Law020_UnusedAsyncDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("async")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!isMemberInPackage(method, "kotlinx.coroutines")) return
        
        var current: UElement? = node
        while (current != null && current !is UMethod) {
            val parent = current.uastParent
            if (parent is ULocalVariable || parent is UReturnExpression || parent is UBinaryExpression || parent is UCallExpression) {
                return 
            }
            if (parent is UBlockExpression || parent is UExpressionList) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Result of 'async' is ignored. Ensure you call 'await()' or handle the Deferred (LAW-020)."
                )
                return
            }
            current = parent
        }
    }

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: return false
        return qualifiedName.startsWith("$packageName.") || qualifiedName == packageName
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "UnusedAsync",
            description = "Async result is ignored",
            rationale = "Ignoring a Deferred result of 'async' is usually a bug or a leak.",
            badExample = "coroutineScope { async { ... } }",
            goodExample = "coroutineScope { val deferred = async { ... } }",
            category = IssueCategory.CONCURRENCY,
            architectureLaw = Law.LAW_020,
            implementation = Implementation(Law020_UnusedAsyncDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
