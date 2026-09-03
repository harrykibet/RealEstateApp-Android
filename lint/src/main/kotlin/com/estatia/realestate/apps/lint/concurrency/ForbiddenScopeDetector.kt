package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Prevents arbitrary creation of CoroutineScopes in production logic (LAW-005).
 * Forces usage of managed scopes (viewModelScope) or injected scopes.
 */
class ForbiddenScopeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("launch", "async", "CoroutineScope", "MainScope")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val methodName = node.methodName ?: return
        val receiver = node.receiver?.asRenderString() ?: ""
        
        // 1. Ban GlobalScope.launch/async
        if (receiver == "GlobalScope") {
            context.report(
                FORBIDDEN_SCOPE_ISSUE,
                node,
                context.getLocation(node),
                "Usage of 'GlobalScope' is forbidden. Use a managed CoroutineScope (e.g., viewModelScope) or an injected application scope (LAW-005)."
            )
            return
        }

        // 2. Ban manual CoroutineScope instantiation in arbitrary business code
        if (methodName == "CoroutineScope" || methodName == "MainScope") {
            val path = context.file.path.replace("\\", "/")
            
            // Allow in dedicated concurrency modules, DI, or initializers
            val isAllowedLocation = path.contains("/di/") || 
                                   path.contains("ConcurrencyModule") || 
                                   path.contains("Initializer") ||
                                   path.contains("/src/test/") ||
                                   path.contains("/src/androidTest/")

            if (!isAllowedLocation) {
                context.report(
                    FORBIDDEN_SCOPE_ISSUE,
                    node,
                    context.getLocation(node),
                    "Manual instantiation of '$methodName' is forbidden in business logic. Inject a managed CoroutineScope via Hilt instead (LAW-005)."
                )
            }
        }
    }

    companion object {
        val FORBIDDEN_SCOPE_ISSUE = EstatiaIssue.create(
            id = "ForbiddenCoroutineScope",
            description = "Unmanaged CoroutineScope detected",
            rationale = """
                Arbitrary CoroutineScopes lead to leaks and non-deterministic lifecycles.
                Scopes must be lifecycle-managed (viewModelScope) or injected.
            """,
            badExample = "val myScope = CoroutineScope(Dispatchers.IO)",
            goodExample = "class MyRepo @Inject constructor(val scope: CoroutineScope)",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-005 (Managed Scopes)",
            implementation = Implementation(ForbiddenScopeDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
        
        // Keep the non-cancellable check but move to its own rule if needed.
        // For now, let's just focus on the strict scope ban.
    }
}
