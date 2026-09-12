package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getParentOfType

/**
 * LAW-007: Production code does not use wall-clock time directly.
 * Time must be injectable via TimeProvider to ensure deterministic testing.
 */
class Law007_DirectSystemTimeProdDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("currentTimeMillis", "now", "nanoTime")

    override fun getApplicableConstructorTypes() = listOf("java.util.Date")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (isTestContext(context)) return
        
        val evaluator = context.evaluator
        val isForbidden = when {
            evaluator.isMemberInClass(method, "java.lang.System") && (method.name == "currentTimeMillis" || method.name == "nanoTime") -> true
            evaluator.isMemberInClass(method, "java.time.Instant") && method.name == "now" -> true
            isMemberInPackage(method, "kotlinx.datetime") && method.name == "now" -> true
            else -> false
        }

        if (isForbidden && !isExempt(context, node)) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Direct usage of system time is forbidden in production (LAW-007). Inject and use a 'TimeProvider' instead."
            )
        }
    }

    override fun visitConstructor(context: JavaContext, node: UCallExpression, constructor: PsiMethod) {
        if (isTestContext(context)) return
        
        if (context.evaluator.isMemberInClass(constructor, "java.util.Date") && !isExempt(context, node)) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Direct usage of system time is forbidden in production (LAW-007). Inject and use a 'TimeProvider' instead."
            )
        }
    }

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: return false
        return qualifiedName.startsWith("$packageName.") || qualifiedName == packageName
    }

    private fun isExempt(context: JavaContext, node: UElement): Boolean {
        // Exempt the TimeProvider implementation itself
        val containingClass = node.getParentOfType<UClass>()
        if (containingClass != null) {
            val name = containingClass.name ?: ""
            if (name.contains("TimeProvider")) return true
            
            // Exempt DI Modules where we might be providing System time as a default
            val annotations = context.evaluator.getAnnotations(containingClass.javaPsi, false)
            if (annotations.any { it.qualifiedName == "dagger.Module" }) return true
        }
        return false
    }

    private fun isTestContext(context: JavaContext): Boolean {
        val path = context.file.path.replace("\\", "/")
        return context.isTestSource || path.contains("/test/") || path.contains("/androidTest/")
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "DirectSystemTimeUsage",
            description = "Direct usage of system time detected",
            rationale = "Relying on system time makes code non-deterministic. Time must be injectable via TimeProvider.",
            badExample = "val now = System.currentTimeMillis()",
            goodExample = "val now = timeProvider.now()",
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PLATFORM,
            architectureLaw = Law.LAW_007,
            implementation = Implementation(Law007_DirectSystemTimeProdDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
