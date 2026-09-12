package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.RuleOwner
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * LAW-009: Production functions do not silently discard failures.
 * Detects catch blocks that return empty values without logging.
 */
class Law009_FailureSmugglingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UCatchClause::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitCatchClause(node: UCatchClause) {
            val visitor = smuggledValueVisitor(context, node)
            node.body.accept(visitor)
        }
    }

    private fun smuggledValueVisitor(context: JavaContext, catchClause: UCatchClause) = object : AbstractUastVisitor() {
        override fun visitReturnExpression(node: UReturnExpression): Boolean {
            val jumpValue = node.returnExpression
            if (isSmuggledValue(jumpValue)) {
                if (!hasLogged(catchClause)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Potential 'failure smuggling' detected in catch block (LAW-009)."
                    )
                }
            }
            return super.visitReturnExpression(node)
        }

        private fun isSmuggledValue(expression: UExpression?): Boolean {
            if (expression == null) return false
            if (expression is ULiteralExpression) {
                val value = expression.value
                return value == null || value == "" || value == 0 || value == false
            }
            
            if (expression is UCallExpression) {
                val name = expression.methodName
                return name == "emptyList" || name == "emptyMap" || name == "emptySet"
            }
            return false
        }

        private fun hasLogged(catchClause: UCatchClause): Boolean {
            var logged = false
            catchClause.body.accept(object : AbstractUastVisitor() {
                override fun visitCallExpression(node: UCallExpression): Boolean {
                    val resolved = node.resolve()
                    val clazz = resolved?.containingClass?.qualifiedName ?: ""
                    if (clazz.contains("Log") || clazz.contains("Timber")) {
                        logged = true
                    }
                    return super.visitCallExpression(node)
                }
            })
            return logged
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "FailureSmuggling",
            description = "Catch block silently discards or hides failure",
            rationale = "Returning empty collections in catch blocks hides failures.",
            badExample = "catch (e: Exception) { return emptyList() }",
            goodExample = "catch (e: Exception) { return AppResult.Error(e) }",
            category = IssueCategory.API_DESIGN,
            architectureLaw = Law.LAW_009,
            implementation = Implementation(Law009_FailureSmugglingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
