package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Prevents usage of forbidden scopes like [GlobalScope] or [NonCancellable].
 */
class ForbiddenScopeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("launch", "async", "withContext")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val receiver = node.receiver?.asRenderString() ?: ""
        
        if (receiver == "GlobalScope") {
            context.report(
                GLOBAL_SCOPE_ISSUE,
                node,
                context.getLocation(node),
                "Usage of 'GlobalScope' is forbidden. Use a managed CoroutineScope (e.g., viewModelScope or custom scope) instead."
            )
        }

        if (node.methodName == "withContext") {
            val firstArg = node.valueArguments.firstOrNull()?.asRenderString() ?: ""
            if (firstArg == "NonCancellable") {
                context.report(
                    NON_CANCELLABLE_ISSUE,
                    node,
                    context.getLocation(node),
                    "Usage of 'NonCancellable' is discouraged. Ensure it is only used for essential cleanup logic."
                )
            }
        }
    }

    companion object {
        private val IMPLEMENTATION = Implementation(ForbiddenScopeDetector::class.java, Scope.JAVA_FILE_SCOPE)

        val GLOBAL_SCOPE_ISSUE = EstatiaIssue.create(
            id = "ForbiddenGlobalScope",
            description = "Usage of GlobalScope detected",
            explanation = """
                GlobalScope makes it easy to leak coroutines because it is not tied to any 
                lifecycle. Always use a managed CoroutineScope that matches the lifecycle 
                of your component.
            """,
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            implementation = IMPLEMENTATION
        )

        val NON_CANCELLABLE_ISSUE = EstatiaIssue.create(
            id = "DiscouragedNonCancellable",
            description = "Usage of NonCancellable context",
            explanation = """
                NonCancellable prevents a coroutine from being cancelled. It should only be 
                used for short, atomic cleanup operations (e.g., closing a database or file).
            """,
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PLATFORM,
            implementation = IMPLEMENTATION
        )
    }
}
