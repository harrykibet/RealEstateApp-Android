package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.PsiMethod

/**
 * Enforces structured concurrency patterns.
 * Detects:
 * 1. Suspend functions launching fire-and-forget work (Secret Concurrency).
 * 2. async calls where the Deferred result is ignored.
 * 3. CoroutineExceptionHandler used in withContext (where it's ignored).
 */
class StructuredConcurrencyDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("launch", "async", "withContext")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val methodName = node.methodName ?: return
        
        when (methodName) {
            "launch", "async" -> {
                checkSecretConcurrency(context, node)
                if (methodName == "async") checkUnusedDeferred(context, node)
            }
            "withContext" -> checkMisplacedExceptionHandler(context, node)
        }
    }

    /**
     * LAW-018: Suspend functions must not secretly launch independent work.
     * They must be sequential and only return when their work is complete.
     */
    private fun checkSecretConcurrency(context: JavaContext, node: UCallExpression) {
        val containingMethod = node.getParentOfType<UMethod>() ?: return
        if (context.evaluator.isSuspend(containingMethod)) {
            // If it's a suspend function and it's calling launch/async on an external scope
            val receiver = node.receiver
            if (receiver != null) {
                // If it has a receiver (e.g. scope.launch), it's launching in that scope
                // instead of using coroutineScope { } or being sequential.
                context.report(
                    SECRET_CONCURRENCY_ISSUE,
                    node,
                    context.getLocation(node),
                    "Suspend function '${containingMethod.name}' secretly launches independent work. " +
                            "Suspend functions should be sequential. Use 'coroutineScope { }' for internal concurrency " +
                            "or remove the launch/async to follow structured concurrency."
                )
            }
        }
    }

    /**
     * async {} without await() is a leak and usually a bug.
     */
    private fun checkUnusedDeferred(context: JavaContext, node: UCallExpression) {
        val parent = node.uastParent
        if (parent is UBlockExpression || parent is UExpressionList) {
            // The result of async (Deferred) is not assigned to a variable or used as a return value
            context.report(
                UNUSED_ASYNC_ISSUE,
                node,
                context.getLocation(node),
                "Result of 'async' is ignored. If you don't need the result, use 'launch'. " +
                        "Otherwise, ensure you call 'await()' on the returned Deferred."
            )
        }
    }

    /**
     * CoroutineExceptionHandler only works on the root coroutine of a scope.
     * In withContext, it is silently ignored.
     */
    private fun checkMisplacedExceptionHandler(context: JavaContext, node: UCallExpression) {
        val arguments = node.valueArguments
        if (arguments.isNotEmpty()) {
            val contextArg = arguments[0].asRenderString()
            if (contextArg.contains("CoroutineExceptionHandler")) {
                context.report(
                    MISPLACED_HANDLER_ISSUE,
                    node,
                    context.getLocation(node),
                    "CoroutineExceptionHandler used in 'withContext' will be ignored. " +
                            "Handle exceptions using try-catch inside the block or move the handler to the root scope."
                )
            }
        }
    }

    companion object {
        val SECRET_CONCURRENCY_ISSUE = EstatiaIssue.create(
            id = "SecretConcurrency",
            description = "Suspend function launches fire-and-forget work",
            explanation = """
                Suspend functions must follow structured concurrency. They should be 
                sequential and only return when their work (and children) is complete. 
                Launching work in an external scope inside a suspend function breaks 
                caller expectations and makes error handling impossible.
            """,
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(StructuredConcurrencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val UNUSED_ASYNC_ISSUE = EstatiaIssue.create(
            id = "UnusedAsync",
            description = "Async result is ignored",
            explanation = """
                The 'async' builder is used to perform work that returns a value (Deferred). 
                Ignoring this result means you are launching a coroutine without awaiting 
                its completion or handling its potential failures. Use 'launch' for 
                fire-and-forget work.
            """,
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(StructuredConcurrencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val MISPLACED_HANDLER_ISSUE = EstatiaIssue.create(
            id = "MisplacedCoroutineExceptionHandler",
            description = "CoroutineExceptionHandler used in withContext",
            explanation = """
                CoroutineExceptionHandler is only used by the coroutine machinery when 
                searching for the root coroutine's exception handler. It has no effect 
                 when passed to 'withContext' or other child coroutine builders.
            """,
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(StructuredConcurrencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
