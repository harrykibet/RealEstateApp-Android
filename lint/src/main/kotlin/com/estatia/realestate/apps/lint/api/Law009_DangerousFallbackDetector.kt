package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * LAW-009: Production functions do not silently discard failures.
 * Detects dangerous elvis operator fallbacks.
 */
class Law009_DangerousFallbackDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UBinaryExpression::class.java, UPolyadicExpression::class.java, UIfExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitBinaryExpression(node: UBinaryExpression) {
            val opText = node.operator.text
            if (opText == "?:" || node.asSourceString().contains("?:")) {
                checkFallback(node.rightOperand, node)
            }
        }

        override fun visitPolyadicExpression(node: UPolyadicExpression) {
            val opText = node.operator.text
            if (opText == "?:" || node.asSourceString().contains("?:")) {
                node.operands.lastOrNull()?.let { checkFallback(it, node) }
            }
        }

        override fun visitIfExpression(node: UIfExpression) {
            // In Kotlin UAST, elvis often manifests as an If expression with a null check
            // We check the source string for '?:' to be sure it's an elvis.
            val src = node.asSourceString()
            if (src.contains("?:")) {
                val fallback = node.elseExpression
                if (fallback != null) {
                    checkFallback(fallback, node)
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
        var current = expression
        while (current is UParenthesizedExpression) {
            current = current.expression
        }
        
        if (current is ULiteralExpression) {
            val value = current.value
            return value == null || (value is String && value.isEmpty()) || value == 0 || value == false
        }
        
        if (current is UCallExpression) {
            val name = current.methodName
            return name == "emptyList" || name == "emptyMap" || name == "emptySet"
        }
        
        if (current is UQualifiedReferenceExpression) {
            return isDangerousFallback(current.selector)
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
            tier = IssueTier.CONVENTION,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_009,
            implementation = Implementation(Law009_DangerousFallbackDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
