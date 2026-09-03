package com.estatia.realestate.apps.lint.concurrency

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
        val methodName = method.name
        
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
     */
    private fun checkSecretConcurrency(context: JavaContext, node: UCallExpression) {
        val containingMethod = node.getParentOfType<UMethod>() ?: return
        if (context.evaluator.isSuspend(containingMethod)) {
            val receiver = node.receiver
            if (receiver != null) {
                context.report(
                    SECRET_CONCURRENCY_ISSUE,
                    node,
                    context.getLocation(node),
                    "Suspend function '${containingMethod.name}' secretly launches independent work. Suspend functions should be sequential (LAW-018)."
                )
            }
        }
    }

    /**
     * async {} without await() is a leak and usually a bug.
     */
    private fun checkUnusedDeferred(context: JavaContext, node: UCallExpression) {
        var current: UElement? = node
        // Walk up to find if we are part of an assignment or return
        while (current != null && current !is UMethod) {
            val parent = current.uastParent
            if (parent is ULocalVariable || parent is UReturnExpression || parent is UBinaryExpression || parent is UCallExpression) {
                return // Used
            }
            if (parent is UBlockExpression || parent is UExpressionList) {
                // If the direct parent is a block/list and we haven't hit an assignment, it's likely unused
                context.report(
                    UNUSED_ASYNC_ISSUE,
                    node,
                    context.getLocation(node),
                    "Result of 'async' is ignored. Ensure you call 'await()' or handle the Deferred (LAW-019)."
                )
                return
            }
            current = parent
        }
    }

    /**
     * CoroutineExceptionHandler only works on the root coroutine of a scope.
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
                    "CoroutineExceptionHandler used in 'withContext' will be ignored (LAW-021)."
                )
            }
        }
    }

    companion object {
        val SECRET_CONCURRENCY_ISSUE = EstatiaIssue.create(
            id = "SecretConcurrency",
            description = "Suspend function launches fire-and-forget work",
            rationale = "Suspend functions should be sequential and follow structured concurrency.",
            badExample = "suspend fun doWork() { scope.launch { ... } }",
            goodExample = "suspend fun doWork() = coroutineScope { launch { ... } }",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-018 (Structured Concurrency)",
            implementation = Implementation(StructuredConcurrencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val UNUSED_ASYNC_ISSUE = EstatiaIssue.create(
            id = "UnusedAsync",
            description = "Async result is ignored",
            rationale = "Ignoring a Deferred result of 'async' is usually a bug or a leak.",
            badExample = "coroutineScope { async { ... } }",
            goodExample = "coroutineScope { val deferred = async { ... } }",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-019 (Explicit Join)",
            implementation = Implementation(StructuredConcurrencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val MISPLACED_HANDLER_ISSUE = EstatiaIssue.create(
            id = "MisplacedCoroutineExceptionHandler",
            description = "CoroutineExceptionHandler used in withContext",
            rationale = "CEH only works on root coroutines and is ignored in withContext.",
            badExample = "withContext(Dispatchers.IO + ceh) { ... }",
            goodExample = "CoroutineScope(Dispatchers.Main + ceh).launch { ... }",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-021 (Exception Handling)",
            implementation = Implementation(StructuredConcurrencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
