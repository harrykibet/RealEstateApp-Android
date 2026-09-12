package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.RuleOwner
import com.intellij.psi.PsiType
import org.jetbrains.uast.*

/**
 * LAW-009: Production functions do not silently discard failures (Estatia Convention).
 * Mandates Result containers for public repository and service methods.
 */
class Law009_ResultWrapperDetector : Detector(), SourceCodeScanner {

    private val exemptSimpleTypes = setOf(
        "java.lang.String", "kotlin.String", "String",
        "int", "java.lang.Integer", "kotlin.Int", "Int",
        "long", "java.lang.Long", "kotlin.Long", "Long",
        "boolean", "java.lang.Boolean", "kotlin.Boolean", "Boolean",
        "double", "java.lang.Double", "kotlin.Double", "Double",
        "float", "java.lang.Float", "kotlin.Float", "Float"
    )

    private val targetAnnotations = setOf(
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Service",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            if (node.isConstructor || !context.evaluator.isPublic(node)) return
            
            val containingClass = node.containingClass ?: return
            
            val isBoundaryComponent = context.evaluator.getAnnotations(containingClass, false)
                .any { targetAnnotations.contains(it.qualifiedName) }

            if (isBoundaryComponent) {
                val returnType = node.returnType ?: return
                
                if (isExempt(node, returnType)) return

                if (!isWrapped(returnType, context, node)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Public repository/service method should return a wrapped Result type (LAW-009)."
                    )
                }
            }
        }
    }

    private fun isExempt(node: UMethod, type: PsiType): Boolean {
        // Non-suspend functions returning simple types are exempt (likely property getters or simple checks)
        val isSuspend = node.sourcePsi?.text?.contains("suspend") == true
        return !isSuspend && exemptSimpleTypes.contains(type.canonicalText)
    }

    private fun isWrapped(type: PsiType, context: JavaContext, node: UMethod): Boolean {
        val canonical = type.canonicalText
        if (canonical == "unit" || canonical == "void" || canonical == "java.lang.Void" || canonical == "kotlin.Unit") return true
        
        val typeName = type.presentableText
        if (typeName.endsWith("Result") || typeName.endsWith("Flow")) return true

        val psiClass = context.evaluator.getTypeClass(type) ?: return false
        val qualifiedName = psiClass.qualifiedName ?: ""
        
        return qualifiedName.endsWith("Result") || 
               qualifiedName.endsWith("Flow") || 
               context.evaluator.inheritsFrom(psiClass, "kotlinx.coroutines.flow.Flow", false)
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MissingResultWrapper",
            description = "Unwrapped return type in Repository/Service",
            rationale = "Estatia convention prefers Result containers to ensure failure modes are handled explicitly. " +
                        "Trivial getters returning simple types (String, Int, Boolean) are exempt if not suspending.",
            badExample = "suspend fun load(): User",
            goodExample = "suspend fun load(): AppResult<User>",
            category = IssueCategory.API_DESIGN,
            architectureLaw = Law.LAW_009,
            implementation = Implementation(Law009_ResultWrapperDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
