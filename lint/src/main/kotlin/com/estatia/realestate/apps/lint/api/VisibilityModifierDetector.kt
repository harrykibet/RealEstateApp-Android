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
            val source = node.sourcePsi?.text ?: ""
            if (!hasVisibilityModifier(source) && !node.isInterface && node.name != null) {
                val element = node as UElement
                context.report(
                    ISSUE,
                    element,
                    context.getLocation(element),
                    "Explicit visibility modifier (public, internal, private) is required for class '${node.name}' (LAW-008)."
                )
            }
        }

        override fun visitMethod(node: UMethod) {
            if (node.isConstructor || node.containingClass?.isInterface == true) return
            
            val source = node.sourcePsi?.text ?: ""
            if (node.sourcePsi?.language?.id == "kotlin") {
                if (!hasVisibilityModifier(source)) {
                    val element = node as UElement
                    context.report(
                        ISSUE,
                        element,
                        context.getLocation(element),
                        "Explicit visibility modifier is required for method '${node.name}'."
                    )
                }
            }
        }
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
