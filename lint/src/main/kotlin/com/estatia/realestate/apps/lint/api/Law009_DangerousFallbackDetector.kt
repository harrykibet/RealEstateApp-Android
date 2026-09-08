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
 * 
 * Detects dangerous elvis operator fallbacks (e.g., ?: emptyList(), ?: "") 
 * which often convert underlying infrastructure failures into silent empty states.
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
            // Some UAST versions represent Elvis as an If expression
            val src = node.asSourceString()
            if (src.contains("?:")) {
                node.elseExpression?.let { checkFallback(it, node) }
            }
        }

        private fun checkFallback(expression: UExpression, node: UElement) {
            if (isDangerousFallback(expression)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Dangerous fallback detected (LAW-009). This default value silently consumes potential failures."
                )
            }
        }
    }

    private fun isDangerousFallback(expression: UExpression): Boolean {
        var current = expression
        while (current is UParenthesizedExpression) {
            current = current.expression
        }
        
        // 1. Literal Check
        if (current is ULiteralExpression) {
            val value = current.value
            return value == null || 
                   (value is String && value.isEmpty()) || 
                   value == 0 || 
                   value == false
        }
        
        // 2. Collection Builder Check
        if (current is UCallExpression) {
            val name = current.methodName
            return name == "emptyList" || name == "emptyMap" || name == "emptySet" ||
                   name == "listOf" && current.valueArgumentCount == 0 ||
                   name == "mapOf" && current.valueArgumentCount == 0 ||
                   name == "setOf" && current.valueArgumentCount == 0
        }
        
        // 3. Qualified Access to empty collections (e.g., Collections.emptyList())
        if (current is UQualifiedReferenceExpression) {
            return isDangerousFallback(current.selector)
        }
        
        return false
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "DangerousFallback",
            description = "Elvis operator uses dangerous default value",
            rationale = "Using '?: emptyList()' or '?: false' often converts critical system failures into " +
                        "empty states, making bugs nearly impossible to trace in production.",
            badExample = "repo.load() ?: emptyList()",
            goodExample = "repo.load() // returns AppResult and handles Error explicitly",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.CONVENTION,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_009,
            implementation = Implementation(Law009_DangerousFallbackDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
