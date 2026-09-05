package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

/**
 * Enforces explicit visibility modifiers for public-facing components.
 */
class VisibilityModifierDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java, UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node.isInterface || node.name == null) return
            
            if (node.sourcePsi?.language?.id == "kotlin") {
                if (!hasExplicitVisibility(node)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Explicit visibility modifier (public, internal, private) is required for class '${node.name}' (LAW-008)."
                    )
                }
            }
        }

        override fun visitMethod(node: UMethod) {
            if (node.isConstructor || node.containingClass?.isInterface == true) return
            
            if (node.sourcePsi?.language?.id == "kotlin") {
                if (!hasExplicitVisibility(node)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Explicit visibility modifier is required for method '${node.name}'."
                    )
                }
            }
        }
    }

    private fun hasExplicitVisibility(element: UElement): Boolean {
        val source = element.sourcePsi?.text ?: return true
        val header = source.substringBefore("{").substringBefore("=")
        return header.contains("public ") || header.contains("private ") || 
               header.contains("internal ") || header.contains("protected ") ||
               header.trim().startsWith("public ") || header.trim().startsWith("private ") ||
               header.trim().startsWith("internal ") || header.trim().startsWith("protected ")
    }

    private fun hasVisibilityModifier(source: String): Boolean {
        return source.contains("public ") || source.contains("private ") || 
               source.contains("internal ") || source.contains("protected ") ||
               source.trim().startsWith("public ") || source.trim().startsWith("private ") ||
               source.trim().startsWith("internal ") || source.trim().startsWith("protected ")
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MissingVisibilityModifier",
            description = "Explicit visibility modifier required",
            rationale = "Default visibility in Kotlin is public, which often leads to accidental implementation leakage.",
            badExample = "class UserRepo",
            goodExample = "internal class UserRepo",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-008",
            implementation = Implementation(VisibilityModifierDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
