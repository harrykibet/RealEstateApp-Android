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
 * Enforces LAW-025 and LAW-027: Semantic architecture rules for Compose.
 * Detects:
 * 1. Direct Repository/Service/UseCase calls (Architecture leakage).
 * 2. Reading mutable fields from Singletons (Stale state).
 */
class ComposeArchitectureDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UCallExpression::class.java, USimpleNameReferenceExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        
        override fun visitCallExpression(node: UCallExpression) {
            if (!isInsideComposable(context, node)) return
            
            val method = node.resolve() ?: return
            val containingClass = method.containingClass?.qualifiedName ?: ""
            
            // 1. Detect Direct Architecture Leakage (LAW-027)
            // Use Evaluator to check for typical architecture component suffixes if we don't have a base class
            // or better, check if it's in a specific package.
            if (containingClass.endsWith("Repository") || 
                containingClass.endsWith("Service") || 
                containingClass.endsWith("UseCase")) {
                
                context.report(
                    ARCHITECTURE_LEAKAGE_ISSUE,
                    node,
                    context.getLocation(node),
                    "Direct call to architectural component '$containingClass' inside Composable. " +
                            "This work must be managed by a ViewModel to ensure proper lifecycle handling (LAW-027)."
                )
            }
        }

        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            if (!isInsideComposable(context, node)) return

            val resolved = node.resolve()
            val containingClass = (resolved as? PsiMember)?.containingClass ?: return
            val isEstatiaComponent = containingClass.qualifiedName?.startsWith("com.estatia") == true
            if (!isEstatiaComponent) return

            val modifierOwner = resolved as? PsiModifierListOwner ?: return
            val isFinal = modifierOwner.hasModifierProperty(PsiModifier.FINAL)
            
            // In Kotlin, a 'var' in an object appears as a non-final field or a getter/setter
            if (!isFinal) {
                val isStatic = modifierOwner.hasModifierProperty(PsiModifier.STATIC)
                // Also check if the containing class itself is an 'object' (Singleton)
                val isObject = containingClass.fields.any { it.name == "INSTANCE" }

                if (isStatic || isObject) {
                    context.report(
                        MUTABLE_SINGLETON_READ_ISSUE,
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
        val ARCHITECTURE_LEAKAGE_ISSUE = EstatiaIssue.create(
            id = "ComposeArchitectureLeakage",
            description = "Architecture component called in Composable",
            rationale = "Directly calling Repositories in Composables breaks UDF and testability.",
            badExample = "@Composable fun List() { repository.load() }",
            goodExample = "@Composable fun List(data: List<Item>) { ... }",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-027 (UI/Data Decoupling)",
            implementation = Implementation(ComposeArchitectureDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val MUTABLE_SINGLETON_READ_ISSUE = EstatiaIssue.create(
            id = "ComposeMutableSingletonRead",
            description = "Mutable singleton read in Composable",
            rationale = "Reading 'var' from objects is non-observable and leads to stale UI.",
            badExample = "object Config { var value = 0 }\n@Composable fun UI() { Text(Config.value.toString()) }",
            goodExample = "object Config { val value = MutableStateFlow(0) }",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-025 (Observable State)",
            implementation = Implementation(ComposeArchitectureDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
