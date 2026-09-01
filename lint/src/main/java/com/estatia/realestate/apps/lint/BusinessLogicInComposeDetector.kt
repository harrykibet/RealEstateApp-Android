package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*

/**
 * Detector that flags business logic or repository calls inside @Composable functions.
 */
class BusinessLogicInComposeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitCallExpression(node: UCallExpression) {
            val method = node.resolve() ?: return
            
            // If the call is inside a Composable
            if (isInsideComposable(node)) {
                if (isForbiddenInCompose(method)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Forbidden business logic call '${method.name}' inside a Composable. " +
                                "Move this logic to a ViewModel and expose the result via StateFlow."
                    )
                }
            }
        }
    }

    private fun isInsideComposable(node: UElement): Boolean {
        var parent = node.uastParent
        while (parent != null) {
            if (parent is UMethod && parent.annotations.any { it.qualifiedName?.endsWith("Composable") == true }) {
                return true
            }
            parent = parent.uastParent
        }
        return false
    }

    private fun isForbiddenInCompose(method: PsiMethod): Boolean {
        val className = method.containingClass?.qualifiedName ?: ""
        return className.contains("Repository") || 
               className.contains("UseCase") || 
               className.contains("DataSource") ||
               className.contains("Service")
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "BusinessLogicInCompose",
            briefDescription = "Business Logic in UI",
            explanation = """
                Composable functions should only be responsible for rendering state. 
                Adding business logic or calling repositories directly from UI makes 
                the app difficult to test and violates the Unidirectional Data Flow pattern.
                
                Always move logic to the ViewModel layer.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(
                BusinessLogicInComposeDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
