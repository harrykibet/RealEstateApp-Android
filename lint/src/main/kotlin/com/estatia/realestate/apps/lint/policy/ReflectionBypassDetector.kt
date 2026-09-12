package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifierListOwner
import org.jetbrains.uast.*

/**
 * Detects and blocks reflection-based architectural bypasses.
 * Prevents calling private methods or bypassing boundary checks via javaClass.getDeclaredMethod.
 */
class ReflectionBypassDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> = listOf(
        "getDeclaredMethod", "getMethod", "getConstructor", "getDeclaredConstructor",
        "getDeclaredField", "getField", "invoke", "newProxyInstance", "getClass", "forName"
    )

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val methodName = node.methodName ?: return
        
        // 🛡️ REFINEMENT: Aggressive detection of reflection-related method names
        val qualifiedName = method.containingClass?.qualifiedName ?: ""
        
        val isReflectionPackage = qualifiedName.startsWith("java.lang.reflect") || 
                                 qualifiedName == "java.lang.Class"
        
        // Known reflection-inducing methods even if class resolution is partial
        val isReflectionKeyword = methodName == "getDeclaredMethod" || 
                                  methodName == "getDeclaredField" || 
                                  methodName == "newProxyInstance"

        if (isReflectionPackage || isReflectionKeyword) {
            val path = context.file.path.replace("\\", "/")
            
            // Allow reflection only in infrastructure/testing or with explicit authorization
            val isGovernedModule = path.contains("/feature/") || 
                                  path.contains("/core/domain/") || 
                                  path.contains("/core/data/") ||
                                  path.contains("/adversarial/") || // 🛡️ REFINEMENT: Explicitly governed for canaries
                                  isInsideArchGovernedComponent(context, node)

            if (isGovernedModule && !context.isTestSource) {
                context.report(
                    ISSUE,
                    node as UElement,
                    context.getLocation(node as UElement),
                    "Reflection-based architectural bypass detected via '$methodName'. " +
                    "Direct reflection is forbidden in governed layers to maintain architectural integrity (LAW-027)."
                )
            }
        }
    }

    private fun isInsideArchGovernedComponent(context: JavaContext, node: UElement): Boolean {
        var current: UElement? = node
        while (current != null) {
            if (current is UDeclaration) {
                val psi = current.javaPsi as? PsiModifierListOwner
                if (psi != null) {
                    val annotations = context.evaluator.getAnnotations(psi, false)
                    val hasArchAnnotation = annotations.any { it.qualifiedName?.contains("com.estatia.realestate.apps.core.architecture.annotations.") == true }
                    if (hasArchAnnotation) return true
                }
            }
            current = current.uastParent
        }
        return false
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ReflectionBypass",
            description = "Reflection-based architectural bypass",
            rationale = "Using reflection to access private members or bypass layer boundaries " +
                        "undermines the entire architectural enforcement system.",
            badExample = "repo.javaClass.getDeclaredMethod(\"privateAction\").invoke(repo)",
            goodExample = "Use public APIs and proper dependency injection.",
            category = IssueCategory.ARCHITECTURE,
            architectureLaw = Law.LAW_027,
            implementation = Implementation(ReflectionBypassDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
