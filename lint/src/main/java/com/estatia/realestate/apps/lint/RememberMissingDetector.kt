package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*

/**
 * Detector that flags usage of state creation (mutableStateOf, derivedStateOf) 
 * inside a @Composable without 'remember'.
 */
class RememberMissingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("mutableStateOf", "derivedStateOf", "mutableListOf", "mutableMapOf")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (isInsideComposable(node)) {
            val parent = node.uastParent
            
            // If the parent is not a 'remember' call, report it
            if (!isWrappedInRemember(parent)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Creation of '${method.name}' inside a Composable without 'remember'. " +
                            "This will lead to performance issues as the object is recreated on every recomposition."
                )
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

    private fun isWrappedInRemember(node: UElement?): Boolean {
        var current = node
        while (current != null && current !is UMethod) {
            if (current is UCallExpression && current.methodName == "remember") {
                return true
            }
            current = current.uastParent
        }
        return false
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "RememberMissing",
            briefDescription = "Missing 'remember' in Compose",
            explanation = """
                Creating state or mutable collections inside a Composable without 'remember' 
                is a common performance pitfall. It causes the state to be reset and heavy 
                objects to be re-allocated on every single recomposition.
                
                Always wrap state creation in 'remember { ... }'.
            """.trimIndent(),
            category = Category.PERFORMANCE,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                RememberMissingDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
