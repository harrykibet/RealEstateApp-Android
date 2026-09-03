package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Enforces usage of Estatia Design System components over raw Material3 components.
 */
class DesignSystemDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> = listOf("Text", "Button")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val className = method.containingClass?.qualifiedName
        
        if (className?.startsWith("androidx.compose.material3") == true) {
            val name = method.name
            val replacement = "Estatia$name"
            
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Using standard Material 3 $name. Use $replacement instead to ensure design system consistency.",
                fix().replace().all().with("com.estatia.realestate.apps.core.designsystem.component.$replacement").build()
            )
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "DesignSystemViolation",
            description = "Standard Material component usage detected",
            explanation = """
                Developers should use Estatia-prefixed design system components instead 
                of raw Material 3 components to maintain visual consistency and support 
                global theme changes.
            """,
            category = IssueCategory.COMPOSE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PRODUCT,
            implementation = Implementation(DesignSystemDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
