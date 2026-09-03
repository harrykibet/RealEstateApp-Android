package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Detects creation of unbounded buffers or caches that could lead to OOM.
 */
class UnboundedBufferDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("buffer", "cache", "MutableSharedFlow")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (node.methodName == "MutableSharedFlow") {
            val replayArg = node.valueArguments.firstOrNull()?.asRenderString() ?: "0"
            if (replayArg.toIntOrNull() ?: 0 > 100) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Large replay buffer detected in SharedFlow. This can lead to excessive memory usage."
                )
            }
        }
        
        if (node.methodName == "buffer") {
            if (node.valueArguments.isEmpty()) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Unbounded buffer detected. Always specify a capacity to prevent backpressure-related memory growth."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "UnboundedBuffer",
            description = "Unbounded or large buffer detected",
            explanation = """
                Unbounded buffers in streams (like Flow.buffer()) or large replay values 
                in SharedFlow can cause memory spikes and OutOfMemoryError under load. 
                Always define explicit bounds for data containers.
            """,
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(UnboundedBufferDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
