package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiType
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UParameter

/**
 * Detector that prevents infrastructure-specific types (Firebase, Amplify, AWS, etc.) 
 * from leaking into domain interfaces (Repositories, UseCases).
 */
class InfrastructureLeakageDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            // Only check interfaces in domain or feature packages
            if (!node.isInterface) return
            val qualifiedName = node.qualifiedName ?: return
            
            if (qualifiedName.contains(".domain.repository") || qualifiedName.contains(".domain.usecase")) {
                node.methods.forEach { method ->
                    checkMethod(context, method)
                }
            }
        }
    }

    private fun checkMethod(context: JavaContext, method: UMethod) {
        // Check return type
        method.returnType?.let { checkType(context, method, it, "Return") }
        
        // Check parameters
        method.uastParameters.forEach { parameter ->
            checkType(context, parameter, parameter.type, "Parameter")
        }
    }

    private fun checkType(context: JavaContext, node: org.jetbrains.uast.UElement, type: PsiType, contextName: String) {
        val canonicalText = type.canonicalText
        if (isInfrastructureType(canonicalText)) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "$contextName type '$canonicalText' leaks infrastructure vocabulary. " +
                        "Domain interfaces must remain agnostic of SDK-specific types. " +
                        "Map this to a domain model or AppResult instead."
            )
        }
    }

    private fun isInfrastructureType(type: String): Boolean {
        return type.contains("com.google.firebase") ||
               type.contains("com.amplifyframework") ||
               type.contains("com.amazonaws") ||
               type.contains("com.mongodb") ||
               type.contains("retrofit2") ||
               type.contains("okhttp3")
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "InfrastructureLeakage",
            briefDescription = "Infrastructure Leakage",
            explanation = """
                Infrastructure types (like 'FirebaseUser', 'AmplifyException', or 'Response') should 
                never be part of a domain interface signature. This violates the Dependency 
                Inversion Principle and couples your business logic to specific 3rd-party vendors.
                
                Always map infrastructure types to agnostic domain models or exceptions at the 
                data layer boundary.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(
                InfrastructureLeakageDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
