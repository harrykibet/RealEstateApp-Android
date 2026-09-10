package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*

/**
 * Prevents arbitrary creation of CoroutineScopes in production logic (LAW-005).
 */
class ForbiddenScopeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("launch", "async", "CoroutineScope", "MainScope")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val methodName = node.methodName ?: return
        
        // Check for GlobalScope
        val receiver = node.receiver
        if (receiver != null) {
            val receiverType = receiver.getExpressionType()
            val receiverClass = context.evaluator.getTypeClass(receiverType)
            if (receiverClass?.qualifiedName == "kotlinx.coroutines.GlobalScope") {
                context.report(
                    FORBIDDEN_SCOPE_ISSUE,
                    node,
                    context.getLocation(node),
                    "Usage of 'GlobalScope' is forbidden (LAW-005)."
                )
                return
            }
        }

        if (methodName == "CoroutineScope" || methodName == "MainScope") {
            if (!isMemberInPackage(method, "kotlinx.coroutines")) return

            val path = context.file.path.replace("\\", "/")
            val isTestContext = path.contains("/test/") || path.contains("/androidTest/") || context.isTestSource
            
            val isAllowed = isTestContext || isInsideAllowedComponent(context, node)

            if (!isAllowed) {
                context.report(
                    FORBIDDEN_SCOPE_ISSUE,
                    node,
                    context.getLocation(node),
                    "Manual instantiation of '$methodName' is forbidden in business logic (LAW-005)."
                )
            }
        }
    }

    private fun isInsideAllowedComponent(context: JavaContext, node: UElement): Boolean {
        var current: UElement? = node
        while (current != null) {
            if (current is UClass) {
                // 1. Check for DI Modules
                val annotations = context.evaluator.getAnnotations(current.javaPsi, false)
                val isDiModule = annotations.any {
                    val name = it.qualifiedName
                    name == "dagger.Module" || name == "dagger.hilt.InstallIn"
                }
                if (isDiModule) return true
                
                // 2. Check for App Initializers (androidx.startup.Initializer)
                if (context.evaluator.inheritsFrom(current, "androidx.startup.Initializer", false)) {
                    return true
                }
            }
            current = current.uastParent
        }
        return false
    }

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: ""
        return qualifiedName.startsWith(packageName)
    }

    companion object {
        val FORBIDDEN_SCOPE_ISSUE = EstatiaIssue.create(
            id = "ForbiddenCoroutineScope",
            description = "Unmanaged CoroutineScope detected",
            rationale = "Arbitrary CoroutineScopes lead to leaks and non-deterministic lifecycles.",
            badExample = "val myScope = CoroutineScope(Dispatchers.IO)",
            goodExample = "class MyRepo @Inject constructor(val scope: CoroutineScope)",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = Law.LAW_005,
            implementation = Implementation(ForbiddenScopeDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
