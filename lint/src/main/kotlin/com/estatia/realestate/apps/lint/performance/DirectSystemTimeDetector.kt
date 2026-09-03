package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Prevents direct usage of System.currentTimeMillis() or Instant.now().
 * Enforces usage of a TimeProvider for testability.
 */
class DirectSystemTimeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("currentTimeMillis", "now")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val className = method.containingClass?.qualifiedName ?: return
        
        if (className == "java.lang.System" || className == "java.time.Instant") {
            // Allow in the implementation of the time provider itself
            if (context.file.name.contains("TimeProvider")) return

            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Direct usage of system time is forbidden. Inject and use a 'TimeProvider' instead to ensure deterministic behavior in tests."
            )
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "DirectSystemTimeUsage",
            description = "Direct usage of system time detected",
            explanation = """
                Relying on system time makes code hard to test deterministically. 
                Always use a TimeProvider interface that can be mocked or controlled in tests.
            """,
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(DirectSystemTimeDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
