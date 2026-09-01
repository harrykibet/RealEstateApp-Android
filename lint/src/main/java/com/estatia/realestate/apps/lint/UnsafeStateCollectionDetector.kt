package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Detector that enforces the use of collectAsStateWithLifecycle() in Compose 
 * instead of the basic collectAsState(), which can leak resources in background.
 */
class UnsafeStateCollectionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("collectAsState")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val className = method.containingClass?.qualifiedName
        
        if (className?.startsWith("androidx.compose.runtime") == true || 
            className?.contains("Flow") == true) {
            
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Using 'collectAsState()' is unsafe in Compose. " +
                        "Use 'collectAsStateWithLifecycle()' instead to ensure the collection stops when the lifecycle is not active."
            )
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "UnsafeStateCollection",
            briefDescription = "Unsafe State Collection in Compose",
            explanation = """
                'collectAsState()' keeps collecting even when the app is in the background, 
                wasting resources and potentially causing issues. 
                
                'collectAsStateWithLifecycle()' automatically pauses collection when the 
                lifecycle state is below STARTED.
            """.trimIndent(),
            category = Category.PERFORMANCE,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                UnsafeStateCollectionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
