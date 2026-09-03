package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UMethod

/**
 * Ensures that repository and service methods return a 'Result' or 'ApiResult' wrapper
 * to force explicit error handling.
 */
class MissingResultWrapperDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            val containingClass = node.containingClass?.qualifiedName ?: return
            
            // Only check repositories and remote services
            if (containingClass.contains("Repository") || containingClass.contains("Service")) {
                if (!context.evaluator.isPublic(node)) return
                
                val returnType = node.returnType?.canonicalText ?: ""
                val isWrapped = returnType.contains("Result") || 
                               returnType.contains("Flow<") || 
                               returnType == "Unit"

                if (!isWrapped) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Public repository/service method '${node.name}' must return a wrapped Result type (e.g., Result<T> or Flow<T>) to ensure explicit error handling."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MissingResultWrapper",
            description = "Unwrapped return type in Repository/Service",
            explanation = """
                To prevent silent failures and ensure consistent error handling across the app, 
                all public methods in the Repository and Service layers must return their 
                data wrapped in a Result container or a reactive stream (Flow).
            """,
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(MissingResultWrapperDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
