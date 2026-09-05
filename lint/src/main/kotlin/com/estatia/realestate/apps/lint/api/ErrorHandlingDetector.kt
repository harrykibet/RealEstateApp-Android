package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiType
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * Enforces LAW-009: "Production functions do not silently discard failures."
 */
class ErrorHandlingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UMethod::class.java, UCatchClause::class.java, UBinaryExpression::class.java, UIfExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        
        override fun visitMethod(node: UMethod) {
            if (node.isConstructor || !context.evaluator.isPublic(node)) return
            
            val containingClass = node.containingClass ?: return
            val className = containingClass.name ?: ""
            
            if (className.endsWith("Repository") || className.endsWith("Service")) {
                val returnType = node.returnType ?: return
                if (!isWrapped(returnType, context)) {
                    context.report(
                        MISSING_WRAPPER_ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Public repository/service method must return a wrapped Result type (LAW-009)."
                    )
                }
            }
        }

        override fun visitCatchClause(node: UCatchClause) {
            val visitor = smuggledValueVisitor(context, node)
            node.body.accept(visitor)
        }

        override fun visitBinaryExpression(node: UBinaryExpression) {
            if (node.operator.text == "?:" || node.asRenderString().contains("?:")) {
                checkFallback(node.rightOperand, node)
            }
        }

        override fun visitIfExpression(node: UIfExpression) {
            // Some Kotlin versions represent Elvis as an If expression
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
                    DANGEROUS_FALLBACK_ISSUE,
                    node,
                    context.getLocation(node),
                    "Dangerous fallback detected (LAW-009)."
                )
            }
        }
    }

    private fun isWrapped(type: PsiType, context: JavaContext): Boolean {
        val canonical = type.canonicalText
        if (canonical == "unit" || canonical == "void" || canonical == "java.lang.Void") return true
        
        val psiClass = context.evaluator.getTypeClass(type) ?: return false
        val qualifiedName = psiClass.qualifiedName ?: ""
        
        return qualifiedName.endsWith("Result") || 
               qualifiedName.endsWith("Flow") || 
               context.evaluator.inheritsFrom(psiClass, "kotlinx.coroutines.flow.Flow", false)
    }

    private fun smuggledValueVisitor(context: JavaContext, catchClause: UCatchClause) = object : AbstractUastVisitor() {
        override fun visitReturnExpression(node: UReturnExpression): Boolean {
            val jumpValue = node.returnExpression
            if (isSmuggledValue(jumpValue)) {
                if (!hasLogged(catchClause)) {
                    context.report(
                        FAILURE_SMUGGLING_ISSUE,
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
        val MISSING_WRAPPER_ISSUE = EstatiaIssue.create(
            id = "MissingResultWrapper",
            description = "Unwrapped return type in Repository/Service",
            rationale = "Prevents silent failures by mandating a Result container.",
            badExample = "fun load(): User",
            goodExample = "fun load(): AppResult<User>",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-009",
            implementation = Implementation(ErrorHandlingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val FAILURE_SMUGGLING_ISSUE = EstatiaIssue.create(
            id = "FailureSmuggling",
            description = "Catch block silently discards or hides failure",
            rationale = "Returning empty collections in catch blocks hides failures.",
            badExample = "catch (e: Exception) { return emptyList() }",
            goodExample = "catch (e: Exception) { return AppResult.Error(e) }",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-009",
            implementation = Implementation(ErrorHandlingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val DANGEROUS_FALLBACK_ISSUE = EstatiaIssue.create(
            id = "DangerousFallback",
            description = "Elvis operator uses dangerous default value",
            rationale = "Using '?: emptyList()' converts system failures into empty states.",
            badExample = "repo.load() ?: emptyList()",
            goodExample = "repo.load() // returns Result",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-009",
            implementation = Implementation(ErrorHandlingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
