package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiType
import org.jetbrains.uast.*

/**
 * LAW-009: Production functions do not silently discard failures.
 * Mandates Result containers for public repository and service methods.
 */
class Law009_ResultWrapperDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            if (node.isConstructor || !context.evaluator.isPublic(node)) return
            
            val containingClass = node.containingClass ?: return
            val className = containingClass.name ?: ""
            
            if (className.endsWith("Repository") || className.endsWith("Service")) {
                val returnType = node.returnType ?: return
                if (!isWrapped(returnType, context, node)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Public repository/service method must return a wrapped Result type (LAW-009)."
                    )
                }
            }
        }
    }

    private fun isWrapped(type: PsiType, context: JavaContext, node: UMethod): Boolean {
        val canonical = type.canonicalText
        if (canonical == "unit" || canonical == "void" || canonical == "java.lang.Void" || canonical == "kotlin.Unit") return true
        
        // 🏎️ CRASH RESILIENCE: Check the type name string directly if resolution would be flaky
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
            rationale = "Prevents silent failures by mandating a Result container.",
            badExample = "fun load(): User",
            goodExample = "fun load(): AppResult<User>",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-009",
            implementation = Implementation(Law009_ResultWrapperDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
