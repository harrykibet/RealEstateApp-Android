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
 * Mandates usage of AppResult wrappers and prevents "failure-smuggling" or "dangerous fallback" patterns.
 */
class ErrorHandlingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UMethod::class.java, UCatchClause::class.java, UBinaryExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        
        /**
         * Enforce AppResult wrappers on public Repository/Service methods.
         */
        override fun visitMethod(node: UMethod) {
            val containingClass = node.containingClass?.qualifiedName ?: return
            if (containingClass.contains("Repository") || containingClass.contains("Service")) {
                if (!context.evaluator.isPublic(node)) return
                
                val returnType = node.returnType?.canonicalText ?: ""
                val isWrapped = returnType.contains("Result") || 
                               returnType.contains("Flow<") || 
                               returnType == "Unit"

                if (!isWrapped) {
                    context.report(
                        MISSING_WRAPPER_ISSUE,
                        node,
                        context.getLocation(node),
                        "Public repository/service method '${node.name}' must return a wrapped Result type (e.g., AppResult<T>) to ensure explicit error handling (LAW-009)."
                    )
                }
            }
        }

        /**
         * Detect "failure-smuggling" patterns in catch blocks.
         */
        override fun visitCatchClause(node: UCatchClause) {
            val body = node.body
            val render = body.asRenderString()
            
            // Check for return/expression values that look like swallowed errors
            val smuggledValues = listOf("null", "emptyList()", "emptyMap()", "emptySet()", "\"\"", "default")
            
            if (smuggledValues.any { render.contains(it) } && !render.contains("Log.") && !render.contains("Timber.")) {
                context.report(
                    FAILURE_SMUGGLING_ISSUE,
                    node,
                    context.getLocation(node),
                    "Potential 'failure smuggling' detected in catch block. Avoid returning empty/null values on error. Use 'AppResult.Error' instead (LAW-009)."
                )
            }
        }

        /**
         * Detect "dangerous fallbacks" using the Elvis operator around infrastructure calls.
         */
        override fun visitBinaryExpression(node: UBinaryExpression) {
            // Check for Elvis operator ?: 
            if (node.operatorIdentifier?.name == "elvis" || node.asRenderString().contains("?:")) {
                val left = node.leftOperand
                val right = node.rightOperand
                
                // 1. Is the left side an infrastructure call?
                val isInfrastructureCall = left is UCallExpression && (
                    left.resolve()?.containingClass?.qualifiedName?.let {
                        it.contains("Repository") || it.contains("Service")
                    } == true
                )

                if (isInfrastructureCall) {
                    val rightRender = right.asRenderString()
                    val dangerousFallbacks = setOf("emptyList()", "emptyMap()", "emptySet()", "0", "\"\"", "false", "null")
                    
                    if (dangerousFallbacks.contains(rightRender)) {
                        context.report(
                            DANGEROUS_FALLBACK_ISSUE,
                            node,
                            context.getLocation(node),
                            "Dangerous fallback detected: using '$rightRender' as a default for an infrastructure call. This can silently turn failures into 'empty' states. Use 'AppResult' to handle errors explicitly (LAW-009)."
                        )
                    }
                }
            }
        }
    }

    companion object {
        val MISSING_WRAPPER_ISSUE = EstatiaIssue.create(
            id = "MissingResultWrapper",
            description = "Unwrapped return type in Repository/Service",
            explanation = """
                To prevent silent failures and ensure consistent error handling, 
                all public methods in the Repository and Service layers must return their 
                data wrapped in a Result container or a reactive stream (Flow).
            """,
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(ErrorHandlingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val FAILURE_SMUGGLING_ISSUE = EstatiaIssue.create(
            id = "FailureSmuggling",
            description = "Catch block silently discards or hides failure",
            explanation = """
                Returning null or empty collections inside a catch block hides the failure 
                from the caller, making it impossible to diagnose issues in production. 
                Always propagate errors using 'AppResult.Error' or map them to domain-specific failures.
            """,
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(ErrorHandlingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val DANGEROUS_FALLBACK_ISSUE = EstatiaIssue.create(
            id = "DangerousFallback",
            description = "Elvis operator uses dangerous default value",
            explanation = """
                Using '?: emptyList()' or similar fallbacks around Repository or Service 
                calls hides potential infrastructure failures. It converts a 'SYSTEM ERROR' 
                into a 'SUCCESS WITH NO DATA' state, which is a semantic bug. 
                Use 'AppResult' to distinguish between success, empty data, and failure.
            """,
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(ErrorHandlingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
