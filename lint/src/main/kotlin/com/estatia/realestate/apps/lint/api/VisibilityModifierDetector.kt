package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.RuleOwner
import org.jetbrains.uast.*

/**
 * Enforces explicit visibility modifiers for public-facing components.
 */
class VisibilityModifierDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java, UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node.isInterface || node.name == null || node is UAnonymousClass) return
            
            if (isKotlin(node) && !hasExplicitVisibility(node)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node as UElement),
                    "Explicit visibility modifier (public, internal, private) is required for class '${node.name}' (LAW-008)."
                )
            }
        }

        override fun visitMethod(node: UMethod) {
            if (node.isConstructor || node.containingClass?.isInterface == true) return
            
            if (isKotlin(node) && !hasExplicitVisibility(node)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node as UElement),
                    "Explicit visibility modifier is required for method '${node.name}'."
                )
            }
        }
    }

    private fun isKotlin(node: UElement): Boolean {
        return node.sourcePsi?.language?.id?.lowercase() == "kotlin"
    }

    private fun hasExplicitVisibility(node: UDeclaration): Boolean {
        val source = node.sourcePsi?.text ?: return true
        val header = source.substringBefore("{").substringBefore("=")
        return header.contains("public ") || 
               header.contains("private ") || 
               header.contains("internal ") || 
               header.contains("protected ") ||
               header.trim().startsWith("public ") || 
               header.trim().startsWith("private ") ||
               header.trim().startsWith("internal ") || 
               header.trim().startsWith("protected ")
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MissingVisibilityModifier",
            description = "Explicit visibility modifier required",
            rationale = "Default visibility in Kotlin is public, which often leads to accidental implementation leakage.",
            badExample = "class UserRepo",
            goodExample = "internal class UserRepo",
            category = IssueCategory.API_DESIGN,
            architectureLaw = Law.LAW_008,
            implementation = Implementation(VisibilityModifierDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
