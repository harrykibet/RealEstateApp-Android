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
        if (node.methodName == "MutableSharedFlow" && isMemberInPackage(method, "kotlinx.coroutines.flow")) {
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
        
        if (node.methodName == "buffer" && isMemberInPackage(method, "kotlinx.coroutines.flow")) {
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

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: return false
        return qualifiedName.startsWith("$packageName.") || qualifiedName == packageName
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "UnboundedBuffer",
            description = "Unbounded or large buffer detected",
            rationale = "Unbounded buffers cause memory spikes and OOM under load.",
            badExample = "flow.buffer()",
            goodExample = "flow.buffer(capacity = 16)",
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-011 (Memory Safety)",
            implementation = Implementation(UnboundedBufferDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
