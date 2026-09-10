package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * LAW-008: Abstraction Boundaries.
 * Ensures that public APIs of repositories and services do not expose infrastructure implementation types.
 */
class ImplementationTypeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            if (node.isConstructor || !context.evaluator.isPublic(node)) return
            
            val containingClass = node.containingClass ?: return
            val className = containingClass.name ?: ""
            
            if (className.endsWith("Repository") || className.endsWith("Service")) {
                // 1. Check Return Type
                node.returnType?.let { checkType(it.canonicalText, node) }
                
                // 2. Check Parameters
                node.uastParameters.forEach { parameter ->
                    checkType(parameter.type.canonicalText, parameter)
                }
            }
        }

        private fun checkType(qualifiedName: String, node: UElement) {
            if (ArchitecturalPolicy.Law003.InfrastructurePackages.any { qualifiedName.startsWith(it) }) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Public API exposes infrastructure type '$qualifiedName' (LAW-008). Use an abstraction instead."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ImplementationTypeInPublicApi",
            description = "Infrastructure type exposed in public API",
            rationale = "Public interfaces should only depend on domain models and abstractions to prevent implementation leakage.",
            badExample = "fun getUser(): FirebaseUser",
            goodExample = "fun getUser(): UserDomainModel",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_008,
            implementation = Implementation(ImplementationTypeDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
