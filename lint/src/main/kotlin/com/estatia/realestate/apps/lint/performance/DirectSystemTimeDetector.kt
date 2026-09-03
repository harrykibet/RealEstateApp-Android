package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Enforces LAW-007 and LAW-014: "Time must be injectable everywhere."
 * Prevents direct usage of System.currentTimeMillis(), Instant.now(), etc., 
 * to ensure deterministic testing via TestClock.
 */
class DirectSystemTimeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("currentTimeMillis", "now", "nanoTime")

    override fun getApplicableConstructorTypes() = listOf("java.util.Date")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val className = method.containingClass?.qualifiedName ?: return
        
        val isForbidden = when (className) {
            "java.lang.System" -> method.name == "currentTimeMillis" || method.name == "nanoTime"
            "java.time.Instant" -> method.name == "now"
            "kotlinx.datetime.Clock.System" -> method.name == "now"
            "kotlinx.datetime.Clock" -> method.name == "now" // For Clock.System.now()
            else -> false
        }

        if (isForbidden) {
            reportIssue(context, node)
        }
    }

    override fun visitConstructor(context: JavaContext, node: UCallExpression, constructor: PsiMethod) {
        if (constructor.containingClass?.qualifiedName == "java.util.Date") {
            reportIssue(context, node)
        }
    }

    private fun reportIssue(context: JavaContext, node: UCallExpression) {
        val path = context.file.path.replace("\\", "/")
        
        // 1. Always allow in TimeProvider implementations or DI modules
        if (context.file.name.contains("TimeProvider") || path.contains("/di/")) return
        
        // 2. LAW-014: Encourage TestClock in tests (reported as WARNING)
        val isTest = path.contains("/src/test/") || path.contains("/src/androidTest/")

        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            if (isTest) {
                "Tests should not depend on real time (LAW-014). Use 'TestClock' or 'TimeProvider' to control time deterministically."
            } else {
                "Direct usage of system time is forbidden in production (LAW-007). Inject and use a 'TimeProvider' instead."
            },
            // Dynamically override severity for tests if needed, 
            // but the registry defines default. 
            // Better to use different issues if severity must differ strictly.
        )
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "DirectSystemTimeUsage",
            description = "Direct usage of system time detected",
            rationale = """
                Relying on system time makes code non-deterministic and hard to test. 
                Time must be injectable via TimeProvider.
            """,
            badExample = "val now = System.currentTimeMillis()",
            goodExample = "val now = timeProvider.now()",
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-007 (Deterministic Time)",
            implementation = Implementation(DirectSystemTimeDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
