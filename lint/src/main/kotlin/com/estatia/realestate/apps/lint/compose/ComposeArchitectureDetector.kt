package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.PsiField
import com.intellij.psi.PsiModifier

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
            if (!isInsideComposable(node)) return
            
            val method = node.resolve() ?: return
            val containingClass = method.containingClass?.qualifiedName ?: ""
            
            // 1. Detect Direct Architecture Leakage (LAW-027)
            if (containingClass.contains("Repository") || 
                containingClass.contains("Service") || 
                containingClass.contains("UseCase")) {
                
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
            if (!isInsideComposable(node)) return

            val resolved = node.resolve()
            if (resolved is PsiField && !resolved.hasModifierProperty(PsiModifier.FINAL)) {
                val containingClass = resolved.containingClass ?: return
                
                // 2. Detect Mutable Singleton Reads (LAW-025)
                // We target members of 'object' declarations (which are static in bytecode)
                val isEstatiaComponent = containingClass.qualifiedName?.contains("com.estatia") == true
                val isStatic = resolved.hasModifierProperty(PsiModifier.STATIC)

                if (isEstatiaComponent && isStatic) {
                    context.report(
                        MUTABLE_SINGLETON_READ_ISSUE,
                        node,
                        context.getLocation(node),
                        "Reading mutable singleton state '${resolved.name}' inside Composable. " +
                                "Compose cannot observe changes to plain 'var' properties. Use StateFlow or MutableState (LAW-025)."
                    )
                }
            }
        }
    }

    private fun isInsideComposable(node: UElement): Boolean {
        val method = node.getParentOfType<UMethod>() ?: return false
        return method.javaPsi.annotations.any { it.qualifiedName?.contains("Composable") == true }
    }

    companion object {
        val ARCHITECTURE_LEAKAGE_ISSUE = EstatiaIssue.create(
            id = "ComposeArchitectureLeakage",
            description = "Architecture component called in Composable",
            explanation = """
                Composables should only depend on UI state and event lambdas. Directly calling 
                Repositories, Services, or UseCases bypasses the ViewModel, making the UI 
                hard to test and breaking the Unidirectional Data Flow.
            """,
            category = IssueCategory.COMPOSE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(ComposeArchitectureDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val MUTABLE_SINGLETON_READ_ISSUE = EstatiaIssue.create(
            id = "ComposeMutableSingletonRead",
            description = "Mutable singleton read in Composable",
            explanation = """
                Reading a plain 'var' from a singleton object inside a Composable is dangerous. 
                Because the property is not observable state, Compose won't recompose when 
                it changes, leading to stale UI and hard-to-debug state inconsistencies.
            """,
            category = IssueCategory.COMPOSE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PRODUCT,
            implementation = Implementation(ComposeArchitectureDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
