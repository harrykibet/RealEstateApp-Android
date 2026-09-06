package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * LAW-015: Tests must not depend on real time.
 * Use 'TestClock' or 'TimeProvider' to control time deterministically in tests.
 */
class Law015_DirectSystemTimeTestDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("currentTimeMillis", "now", "nanoTime")

    override fun getApplicableConstructorTypes() = listOf("java.util.Date")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!isTestContext(context)) return
        
        val evaluator = context.evaluator
        val isForbidden = when {
            evaluator.isMemberInClass(method, "java.lang.System") && (method.name == "currentTimeMillis" || method.name == "nanoTime") -> true
            evaluator.isMemberInClass(method, "java.time.Instant") && method.name == "now" -> true
            isMemberInPackage(method, "kotlinx.datetime") && method.name == "now" -> true
            else -> false
        }

        if (isForbidden) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Tests should not depend on real time (LAW-015). Use 'TestClock' or 'TimeProvider' to control time deterministically."
            )
        }
    }

    override fun visitConstructor(context: JavaContext, node: UCallExpression, constructor: PsiMethod) {
        if (!isTestContext(context)) return
        
        if (context.evaluator.isMemberInClass(constructor, "java.util.Date")) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Tests should not depend on real time (LAW-015). Use 'TestClock' or 'TimeProvider' to control time deterministically."
            )
        }
    }

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: return false
        return qualifiedName.startsWith("$packageName.") || qualifiedName == packageName
    }

    private fun isTestContext(context: JavaContext): Boolean {
        val path = context.file.path.replace("\\", "/")
        return context.isTestSource || path.contains("/src/test/") || path.contains("/src/androidTest/")
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "DirectSystemTimeUsageInTest",
            description = "Direct usage of system time in test",
            rationale = "Tests depending on real time are non-deterministic. Use TestClock instead.",
            badExample = "val now = System.currentTimeMillis()",
            goodExample = "val now = testClock.now()",
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PLATFORM,
            architectureLaw = Law.LAW_015,
            implementation = Implementation(Law015_DirectSystemTimeTestDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
