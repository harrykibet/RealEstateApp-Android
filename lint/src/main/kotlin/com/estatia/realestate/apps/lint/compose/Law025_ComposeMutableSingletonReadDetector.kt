package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.*

/**
 * LAW-025: Observable State.
 * Prevents reading mutable fields from Singletons inside Composables.
 */
class Law025_ComposeMutableSingletonReadDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(USimpleNameReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            if (!isInsideComposable(context, node)) return

            val resolved = node.resolve()
            val containingClass = (resolved as? PsiMember)?.containingClass ?: return
            val isEstatiaComponent = containingClass.qualifiedName?.startsWith("com.estatia") == true
            if (!isEstatiaComponent) return

            val modifierOwner = resolved as? PsiModifierListOwner ?: return
            val isFinal = modifierOwner.hasModifierProperty(PsiModifier.FINAL)
            
            if (!isFinal) {
                val isStatic = modifierOwner.hasModifierProperty(PsiModifier.STATIC)
                val isObject = containingClass.fields.any { it.name == "INSTANCE" }

                if (isStatic || isObject) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Reading mutable singleton state '${(resolved as? PsiNamedElement)?.name}' inside Composable. " +
                                "Compose cannot observe changes to plain 'var' properties. Use StateFlow or MutableState (LAW-025)."
                    )
                }
            }
        }
    }

    private fun isInsideComposable(context: JavaContext, node: UElement): Boolean {
        val method = node.getParentOfType<UMethod>()?.javaPsi ?: return false
        return context.evaluator.getAnnotations(method, false)
            .any { it.qualifiedName == "androidx.compose.runtime.Composable" }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ComposeMutableSingletonRead",
            description = "Mutable singleton read in Composable",
            rationale = "Reading 'var' from objects is non-observable and leads to stale UI.",
            badExample = "object Config { var value = 0 }\n@Composable fun UI() { Text(Config.value.toString()) }",
            goodExample = "object Config { val value = MutableStateFlow(0) }",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-025 (Observable State)",
            implementation = Implementation(Law025_ComposeMutableSingletonReadDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
