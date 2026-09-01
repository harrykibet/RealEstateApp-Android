package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getParentOfType
import org.jetbrains.uast.util.isConstructorCall

/**
 * Detector that prevents usage of GlobalScope or unmanaged CoroutineScopes in 
 * lifecycle-aware components.
 */
class ForbiddenCoroutineScopeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("launch", "async", "CoroutineScope")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        // 1. Check for GlobalScope.launch/async
        val receiver = node.receiver?.asRenderString()
        if (receiver == "GlobalScope") {
            reportIssue(context, node, "GlobalScope")
        }

        // 2. Check for manual CoroutineScope(...) calls inside ViewModels or Repositories
        if (method.name == "CoroutineScope" || node.isConstructorCall()) {
            val containingClass = node.getParentOfType<UClass>()
            val className = containingClass?.qualifiedName
            
            if (className?.endsWith("ViewModel") == true || className?.endsWith("RepositoryImpl") == true) {
                // If it's not being injected (i.e., created locally), report it.
                // This is a heuristic: usually locally created scopes in these classes are leaks.
                reportIssue(context, node, "Unmanaged CoroutineScope")
            }
        }
    }

    private fun reportIssue(context: JavaContext, node: UElement, scopeName: String) {
        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            "Usage of $scopeName is forbidden here. Use lifecycle-aware scopes (e.g., viewModelScope) " +
                    "or an injected CoroutineScope instead to ensure proper cancellation and prevent leaks."
        )
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "ForbiddenCoroutineScope",
            briefDescription = "Forbidden Coroutine Scope",
            explanation = """
                Using 'GlobalScope' or creating unmanaged 'CoroutineScope' instances inside 
                lifecycle-aware components like ViewModels or Repositories leads to memory leaks 
                and violates structured concurrency. 
                
                Always use 'viewModelScope' or inject a managed 'CoroutineScope' that is tied 
                to the application or component lifecycle.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                ForbiddenCoroutineScopeDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
