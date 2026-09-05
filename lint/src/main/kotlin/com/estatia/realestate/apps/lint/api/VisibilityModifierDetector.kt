package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.isKotlin
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.PsiModifier

/**
 * Enforces explicit visibility modifiers for public-facing components.
 */
class VisibilityModifierDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java, UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node.isInterface || node.name == null || node is UAnonymousClass) return
            
            // Evaluator's getVisibility returns effective visibility, 
            // but we want to check if it's EXPLICIT in the source.
            // For Kotlin, we can check the visibility modifiers on the PsiElement.
            if (node.sourcePsi != null && isKotlin(node.sourcePsi!!.language) && !hasExplicitVisibility(node)) {
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
            
            if (isKotlin(node.sourcePsi) && !hasExplicitVisibility(node)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node as UElement),
                    "Explicit visibility modifier is required for method '${node.name}'."
                )
            }
        }
    }

    private fun hasExplicitVisibility(node: UDeclaration): Boolean {
        val modifierList = node.modifierList ?: return false
        return modifierList.hasModifierProperty(PsiModifier.PUBLIC) ||
               modifierList.hasModifierProperty(PsiModifier.PRIVATE) ||
               modifierList.hasModifierProperty(PsiModifier.PROTECTED) ||
               // "internal" is a custom modifier in Kotlin
               node.sourcePsi?.text?.contains("internal ") == true ||
               // If it's public but NOT by default (Kotlin doesn't have a way to check 'explicit public' 
               // via standard PsiModifier without checking the text or tokens)
               node.sourcePsi?.text?.contains("public ") == true
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
