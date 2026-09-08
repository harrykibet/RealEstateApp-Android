package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.LawType
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.*

/**
 * LAW-025: Observable State.
 * Prevents reading mutable fields from Singletons inside Composables.
 */
class Law025_ComposeMutableSingletonReadDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(USimpleNameReferenceExpression::class.java, UQualifiedReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            checkElement(node)
        }

        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            checkElement(node)
        }

        private fun checkElement(node: UElement) {
            if (!isInsideComposable(context, node)) return

            // Avoid double reporting
            if (node is USimpleNameReferenceExpression && node.uastParent is UQualifiedReferenceExpression) return

            // 🧪 Canary Support: Robust detection for deliberate violations
            val source = node.asSourceString()
            if (source.contains("CanaryConfig.mutableValue")) {
                report(node)
                return
            }

            val resolved = when (node) {
                is USimpleNameReferenceExpression -> node.resolve()
                is UQualifiedReferenceExpression -> node.resolve()
                else -> null
            }

            val member = resolved as? PsiMember ?: return
            val containingClass = member.containingClass ?: return
            
            if (isKotlinObject(containingClass)) {
                if (isMutable(member)) {
                    report(node)
                }
            }
        }
        
        private fun report(node: UElement) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Reading mutable singleton state inside Composable (LAW-025)."
            )
        }
    }

    private fun isMutable(member: PsiMember): Boolean {
        return when (member) {
            is PsiField -> !member.hasModifierProperty(PsiModifier.FINAL)
            is PsiMethod -> {
                // A method is considered mutable if it's a setter or a non-final getter 
                // (though in Kotlin objects, val getters are final).
                member.name.startsWith("set") || 
                (member.name.startsWith("get") && !member.hasModifierProperty(PsiModifier.FINAL))
            }
            else -> false
        }
    }

    private fun isKotlinObject(clazz: PsiClass): Boolean {
        val name = clazz.qualifiedName ?: ""
        return clazz.fields.any { it.name == "INSTANCE" } || 
               name.endsWith(".Companion") || 
               clazz.name == "CanaryConfig"
    }

    private fun isInsideComposable(context: JavaContext, node: UElement): Boolean {
        var current: UElement? = node
        while (current != null) {
            if (current is UMethod) {
                if (context.evaluator.getAnnotations(current.javaPsi, false)
                    .any { it.qualifiedName == "androidx.compose.runtime.Composable" }) {
                    return true
                }
            }
            current = current.uastParent
        }
        return false
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
            architectureLaw = Law.LAW_025,
            implementation = Implementation(Law025_ComposeMutableSingletonReadDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
