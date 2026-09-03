package com.estatia.realestate.apps.lint.security

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Prevents logging of sensitive information like passwords, tokens, or PII.
 */
class SensitiveLoggingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("d", "e", "i", "v", "w", "log")

    private val sensitiveKeywords = listOf("password", "token", "secret", "apikey", "email", "phone")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val evaluator = context.evaluator
        if (evaluator.isMemberInClass(method, "android.util.Log") || 
            evaluator.isMemberInClass(method, "timber.log.Timber")) {
            
            val arguments = node.valueArguments.map { it.asRenderString().lowercase() }
            if (arguments.any { arg -> sensitiveKeywords.any { keyword -> arg.contains(keyword) } }) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Potential exposure of sensitive data in logs. Avoid logging identifiers like 'password', 'token', or 'email'."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "SensitiveLogging",
            description = "Sensitive data found in logs",
            explanation = """
                Logging sensitive information (passwords, tokens, personal data) is a 
                security risk. These logs can be intercepted or extracted from the device. 
                Always scrub or mask sensitive data before logging.
            """,
            category = IssueCategory.SECURITY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.SECURITY,
            implementation = Implementation(SensitiveLoggingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
