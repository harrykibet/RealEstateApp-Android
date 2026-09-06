package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.PsiMethod

/**
 * LAW-019: Structured Concurrency.
 * Suspend functions must not secretly launch independent work.
 */
class Law019_SecretConcurrencyDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("launch", "async")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!isMemberInPackage(method, "kotlinx.coroutines")) return
        
        val containingMethod = node.getParentOfType<UMethod>() ?: return
        if (context.evaluator.isSuspend(containingMethod)) {
            val receiverType = node.receiverType
            if (receiverType != null && context.evaluator.inheritsFrom(context.evaluator.getTypeClass(receiverType), "kotlinx.coroutines.CoroutineScope", false)) {
                 context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Suspend function '${containingMethod.name}' secretly launches independent work. Suspend functions should be sequential (LAW-019)."
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
            id = "SecretConcurrency",
            description = "Suspend function launches fire-and-forget work",
            rationale = "Suspend functions should be sequential and follow structured concurrency.",
            badExample = "suspend fun doWork() { scope.launch { ... } }",
            goodExample = "suspend fun doWork() = coroutineScope { launch { ... } }",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = Law.LAW_019,
            implementation = Implementation(Law019_SecretConcurrencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
