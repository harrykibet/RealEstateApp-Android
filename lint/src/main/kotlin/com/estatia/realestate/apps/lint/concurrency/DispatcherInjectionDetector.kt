package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import org.jetbrains.uast.UReferenceExpression

/**
 * Prevents hardcoding [Dispatchers.IO], [Dispatchers.Main], or [Dispatchers.Default] (LAW-006).
 * Enforces usage of injected AppDispatchers abstraction for testability.
 */
class DispatcherInjectionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableReferenceNames() = listOf("IO", "Main", "Default", "Unconfined")

    override fun visitReference(context: JavaContext, reference: UReferenceExpression, referenced: PsiElement) {
        if (referenced is PsiField) {
            val containingClass = referenced.containingClass?.qualifiedName
            if (containingClass == "kotlinx.coroutines.Dispatchers") {
                
                val path = context.file.path.replace("\\", "/")
                // Allow only in concurrency module or DI configuration
                val isAllowedLocation = path.contains("/di/") || 
                                       path.contains("DispatchersModule") ||
                                       path.contains("/src/test/") ||
                                       path.contains("/src/androidTest/")

                if (!isAllowedLocation) {
                    context.report(
                        ISSUE,
                        reference,
                        context.getLocation(reference),
                        "Hardcoded Dispatcher '${referenced.name}' is forbidden. Inject 'AppDispatchers' or use Estatia qualifiers instead (LAW-006)."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "HardcodedDispatcher",
            description = "Hardcoded Coroutine Dispatcher detected",
            explanation = """
                Hardcoding dispatchers makes it impossible to swap them during unit testing, 
                leading to flaky tests and slow build verification.
                
                Estatia requires all dispatchers to be provided via the 'AppDispatchers' 
                abstraction or injected using specific Hilt qualifiers.
            """,
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(DispatcherInjectionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
