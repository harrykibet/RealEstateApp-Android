package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiField
import org.jetbrains.uast.*

/**
 * Prevents hardcoding [Dispatchers.IO], [Dispatchers.Main], or [Dispatchers.Default] (LAW-006).
 */
class DispatcherInjectionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(USimpleNameReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            val name = node.identifier
            if (name == "IO" || name == "Main" || name == "Default" || name == "Unconfined") {
                val resolved = node.resolve()
                val isDispatcher = (resolved is PsiField &&
                                   resolved.containingClass?.qualifiedName == "kotlinx.coroutines.Dispatchers") ||
                                   node.asRenderString().contains("Dispatchers.")

                if (isDispatcher) {
                    val path = context.file.path.replace("\\", "/")
                    val isAllowed = path.contains("/di/") || path.contains("Module") ||
                                   path.contains("/test/") || path.contains("/androidTest/")

                    if (!isAllowed) {
                        context.report(
                            ISSUE,
                            node,
                            context.getLocation(node),
                            "Hardcoded Dispatcher '$name' is forbidden (LAW-006)."
                        )
                    }
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "HardcodedDispatcher",
            description = "Hardcoded Coroutine Dispatcher detected",
            rationale = "Hardcoding dispatchers prevents swapping them during testing.",
            badExample = "withContext(Dispatchers.IO) { ... }",
            goodExample = "withContext(dispatchers.io) { ... }",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-006",
            implementation = Implementation(DispatcherInjectionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
