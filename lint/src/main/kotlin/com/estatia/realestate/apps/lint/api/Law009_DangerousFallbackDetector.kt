package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * LAW-009: Production functions do not silently discard failures.
 * Detects dangerous elvis operator fallbacks.
 */
class Law009_DangerousFallbackDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UBinaryExpression::class.java, UIfExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitBinaryExpression(node: UBinaryExpression) {
            if (node.operator.text == "?:" || node.asRenderString().contains("?:")) {
                checkFallback(node.rightOperand, node)
            }
        }

        override fun visitIfExpression(node: UIfExpression) {
            val source = node.asRenderString()
            if (source.contains("?:")) {
                val elseExpr = node.elseExpression
                if (elseExpr != null) {
                    checkFallback(elseExpr, node)
                }
            }
        }

        private fun checkFallback(expression: UExpression, node: UElement) {
            if (isDangerousFallback(expression)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Dangerous fallback detected (LAW-009)."
                )
            }
        }
    }

    private fun isDangerousFallback(expression: UExpression): Boolean {
        if (expression is ULiteralExpression) {
            val value = expression.value
            return value == null || value == "" || value == 0 || value == false
        }
        if (expression is UCallExpression) {
            val name = expression.methodName
            return name == "emptyList" || name == "emptyMap" || name == "emptySet"
        }
        if (expression is UQualifiedReferenceExpression) {
            return isDangerousFallback(expression.selector)
        }
        return false
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "DangerousFallback",
            description = "Elvis operator uses dangerous default value",
            rationale = "Using '?: emptyList()' converts system failures into empty states.",
            badExample = "repo.load() ?: emptyList()",
            goodExample = "repo.load() // returns Result",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-009",
            implementation = Implementation(Law009_DangerousFallbackDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
