package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
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

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(USimpleNameReferenceExpression::class.java, UQualifiedReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            checkElement(context, node)
        }

        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            checkElement(context, node)
        }

        private fun checkElement(context: JavaContext, node: UElement) {
            if (!isInsideComposable(context, node)) return

            // Avoid double reporting
            if (node is USimpleNameReferenceExpression && node.uastParent is UQualifiedReferenceExpression) return

            val resolved = when (node) {
                is USimpleNameReferenceExpression -> node.resolve()
                is UQualifiedReferenceExpression -> node.resolve()
                else -> null
            }

            val member = resolved as? PsiMember ?: return
            val containingClass = member.containingClass ?: return
            
            if (isKotlinObject(context, containingClass)) {
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

        private fun isMutable(member: PsiMember): Boolean {
            return when (member) {
                is PsiField -> !member.hasModifierProperty(PsiModifier.FINAL)
                is PsiMethod -> {
                    val name = member.name
                    if (name.startsWith("set")) return true
                    if (name.startsWith("get")) {
                        val containingClass = member.containingClass ?: return false
                        val setterName = name.replaceFirst("get", "set")
                        val hasSetter = containingClass.findMethodsByName(setterName, false).isNotEmpty()
                        return hasSetter
                    }
                    false
                }
                else -> false
            }
        }

        private fun isKotlinObject(context: JavaContext, clazz: PsiClass): Boolean {
            // 🛡️ SEMANTIC RESOLUTION: Detect Kotlin Singleton Objects or Companions
            val qualifiedName = clazz.qualifiedName ?: ""
            return clazz.fields.any { it.name == "INSTANCE" } || 
                   qualifiedName.endsWith(".Companion") ||
                   context.evaluator.getAnnotations(clazz, false).any { it.qualifiedName?.contains("Singleton") == true }
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
