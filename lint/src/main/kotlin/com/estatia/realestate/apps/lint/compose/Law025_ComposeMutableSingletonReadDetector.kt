package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.*

/**
 * LAW-025: Observable State.
 * Prevents reading mutable fields from Singletons inside Composables.
 */
class Law025_ComposeMutableSingletonReadDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(USimpleNameReferenceExpression::class.java, UCallExpression::class.java, UQualifiedReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            checkMember(node, node.resolve())
        }

        override fun visitCallExpression(node: UCallExpression) {
            checkMember(node, node.resolve())
        }

        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            checkMember(node, node.resolve())
        }

        private fun checkMember(node: UElement, resolved: PsiElement?) {
            if (!isInsideComposable(context, node)) return

            val member = resolved as? PsiMember
            val containingClass = member?.containingClass ?: (resolved as? PsiClass)
            
            val className = containingClass?.qualifiedName ?: ""
            val isEstatiaComponent = className.contains("com.estatia")
            
            val receiverString = when (node) {
                is UQualifiedReferenceExpression -> node.receiver.asRenderString()
                is USimpleNameReferenceExpression -> (node.uastParent as? UQualifiedReferenceExpression)?.receiver?.asRenderString() ?: ""
                else -> ""
            }
            
            val isLikelySingleton = isEstatiaComponent || (receiverString.isNotEmpty() && receiverString.firstOrNull()?.isUpperCase() == true)

            if (isLikelySingleton) {
                val isMutable = when (member) {
                    is PsiField -> !member.hasModifierProperty(PsiModifier.FINAL)
                    is PsiMethod -> (member.name.startsWith("get") || member.name.startsWith("set")) && !member.hasModifierProperty(PsiModifier.FINAL)
                    null -> node.asRenderString().firstOrNull()?.isLowerCase() == true
                    else -> false
                }
                
                if (isMutable) {
                    val isObject = containingClass?.let { isKotlinObject(it) } ?: (receiverString.isNotEmpty() && !receiverString.contains("("))

                    if (isObject) {
                        context.report(
                            ISSUE,
                            node,
                            context.getLocation(node),
                            "Reading mutable singleton state inside Composable. " +
                                "Compose cannot observe changes to plain 'var' properties. Use StateFlow or MutableState (LAW-025)."
                        )
                    }
                }
            }
        }
    }

    private fun isKotlinObject(clazz: PsiClass): Boolean {
        return clazz.fields.any { it.name == "INSTANCE" } || clazz.qualifiedName?.endsWith(".Companion") == true
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
