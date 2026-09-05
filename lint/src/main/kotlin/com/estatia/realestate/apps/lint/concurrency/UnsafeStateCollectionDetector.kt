package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField

/**
 * Detects usage of MutableStateFlow or other state containers inside standard collections.
 * 
 * LAW-012: Observable state must be managed correctly.
 */
class UnsafeStateCollectionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val type = node.type
            if (type !is PsiClassType) return
            
            val typeClass = context.evaluator.getTypeClass(type) ?: return
            
            val isCollection = context.evaluator.inheritsFrom(typeClass, "java.util.Collection", false) ||
                               context.evaluator.inheritsFrom(typeClass, "java.util.Map", false)
            
            if (isCollection) {
                // Check generic arguments recursively
                if (hasNestedStateContainer(type, context)) {
                     context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "State containers inside collections detected. Use 'mutableStateListOf()' or a dedicated State holder instead."
                    )
                }
            }
        }
    }

    private fun hasNestedStateContainer(type: PsiType, context: JavaContext): Boolean {
        if (type !is PsiClassType) return false
        
        val fqn = type.canonicalText
        if (fqn.contains("MutableStateFlow") || fqn.contains("MutableState") || fqn.contains("MutableSharedFlow")) {
            return true
        }
        
        // Recursively check type parameters (e.g. List<MutableStateFlow<Int>>)
        return type.parameters.any { hasNestedStateContainer(it, context) }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "UnsafeStateCollection",
            description = "State containers inside standard collections",
            rationale = "Wrapping StateFlows in collections makes observation difficult.",
            badExample = "val states = listOf(MutableStateFlow(0))",
            goodExample = "val state = mutableStateListOf<Int>()",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-012 (Observable State)",
            implementation = Implementation(UnsafeStateCollectionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
