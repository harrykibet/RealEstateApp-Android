package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Enforces usage of Estatia Design System components over raw Material3 components.
 */
class DesignSystemDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UCallExpression::class.java, UImportStatement::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitImportStatement(node: UImportStatement) {
            val path = node.importReference?.asRenderString() ?: ""
            if (path.contains("androidx.compose.material3") && (path.endsWith(".Text") || path.endsWith(".Button"))) {
                report(context, node, path.split(".").last())
            }
        }

        override fun visitCallExpression(node: UCallExpression) {
            val name = node.methodName ?: ""
            if (name == "Text" || name == "Button") {
                val clazz = node.resolve()?.containingClass?.qualifiedName ?: ""
                if (clazz.contains("androidx.compose.material3") || node.asRenderString().contains("androidx.compose.material3")) {
                    report(context, node, name)
                }
            }
        }
    }

    private fun report(context: JavaContext, node: UElement, name: String) {
        val replacement = "Estatia$name"
        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            "Using standard Material 3 $name. Use $replacement instead (LAW-022)."
        )
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "DesignSystemViolation",
            description = "Standard Material component usage detected",
            rationale = "Use Estatia-prefixed components to maintain visual consistency.",
            badExample = "Button(onClick = { ... }) { ... }",
            goodExample = "EstatiaButton(onClick = { ... }) { ... }",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-022",
            implementation = Implementation(DesignSystemDetector::class.java, Scope.JAVA_FILE_SCOPE),
            autofixAvailable = true
        )
    }
}
