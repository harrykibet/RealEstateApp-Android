package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

/**
 * Detector that ensures mutable state (MutableStateFlow, MutableSharedFlow, etc.) 
 * is not exposed publicly, enforcing strict state ownership.
 */
class MutableStateDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UField::class.java, UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        
        override fun visitField(node: UField) {
            if (context.evaluator.isPublic(node)) {
                val type = node.type.canonicalText
                if (isMutableType(type)) {
                    reportIssue(context, node, node.name)
                }
            }
        }

        override fun visitMethod(node: UMethod) {
            // Check for getters or methods returning mutable types publicly
            if (context.evaluator.isPublic(node)) {
                val returnType = node.returnType?.canonicalText ?: return
                if (isMutableType(returnType)) {
                    reportIssue(context, node, node.name)
                }
            }
        }
    }

    private fun isMutableType(type: String): Boolean {
        return type.startsWith("kotlinx.coroutines.flow.MutableStateFlow") ||
               type.startsWith("kotlinx.coroutines.flow.MutableSharedFlow") ||
               type.startsWith("androidx.compose.runtime.MutableState") ||
               type.contains("MutableList") ||
               type.contains("MutableMap") ||
               type.contains("MutableSet") ||
               type.startsWith("java.util.ArrayList") ||
               type.startsWith("java.util.HashMap") ||
               type.startsWith("java.util.HashSet")
    }

    private fun reportIssue(context: JavaContext, node: UElement, name: String) {
        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            "Mutable state '$name' is exposed publicly. This violates Estatia's state ownership standards. " +
                    "Expose as an immutable type (e.g., StateFlow, List) instead."
        )
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "ExposedMutableState",
            briefDescription = "Exposed Mutable State",
            explanation = """
                Mutable state should never be exposed publicly. This ensures that only the owning 
                class can modify its state, preventing "impossible state" bugs and ensuring 
                thread safety.
                
                Always expose state as an immutable type:
                - Use 'StateFlow' instead of 'MutableStateFlow'
                - Use 'List' instead of 'MutableList' or 'ArrayList'
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                MutableStateDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
