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
 * Prevents hardcoding [Dispatchers.IO], [Dispatchers.Main], or [Dispatchers.Default].
 * Enforces injection via @Dispatcher(EstatiaDispatcher.IO).
 */
class DispatcherInjectionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableReferenceNames() = listOf("IO", "Main", "Default", "Unconfined")

    override fun visitReference(context: JavaContext, reference: UReferenceExpression, referenced: PsiElement) {
        if (referenced is PsiField) {
            val containingClass = referenced.containingClass?.qualifiedName
            if (containingClass == "kotlinx.coroutines.Dispatchers") {
                context.report(
                    ISSUE,
                    reference,
                    context.getLocation(reference),
                    "Hardcoded Dispatcher '${referenced.name}' detected. Inject the dispatcher via Hilt using EstatiaDispatchers instead."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "HardcodedDispatcher",
            description = "Hardcoded Coroutine Dispatcher detected",
            explanation = """
                Hardcoding dispatchers makes it impossible to swap them during unit testing.
                Use Hilt to inject dispatchers using EstatiaDispatchers qualifiers.
            """,
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(DispatcherInjectionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
