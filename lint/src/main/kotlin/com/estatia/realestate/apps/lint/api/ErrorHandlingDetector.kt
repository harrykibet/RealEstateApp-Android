package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Enforces LAW-009: "Production functions do not silently discard failures."
 */
class ErrorHandlingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UMethod::class.java, UCatchClause::class.java, UBinaryExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        
        override fun visitMethod(node: UMethod) {
            val text = node.asRenderString()
            if (text.contains("Repository") || text.contains("Service")) {
                if (node.isConstructor) return
                
                val returnType = node.returnType?.canonicalText ?: ""
                val isWrapped = returnType.contains("Result") || 
                               returnType.contains("Flow") || 
                               returnType == "void" || returnType == "Unit" ||
                               text.contains("Result") || text.contains("Flow")

                if (!isWrapped) {
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
            val body = node.body.asRenderString()
            val smuggledValues = listOf("null", "emptyList", "emptyMap", "emptySet", "\"\"", "default")
            
            if (smuggledValues.any { body.contains(it) } && !body.contains("Log.") && !body.contains("Timber.")) {
                context.report(
                    FAILURE_SMUGGLING_ISSUE,
                    node,
                    context.getLocation(node as UElement),
                    "Potential 'failure smuggling' detected in catch block (LAW-009)."
                )
            }
        }

        override fun visitBinaryExpression(node: UBinaryExpression) {
            val text = node.asRenderString()
            if (text.contains("?:")) {
                val dangerous = setOf("emptyList()", "emptyMap()", "0", "\"\"", "false", "null")
                if (dangerous.any { text.contains(it) }) {
                    context.report(
                        DANGEROUS_FALLBACK_ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Dangerous fallback detected (LAW-009)."
                    )
                }
            }
        }
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
