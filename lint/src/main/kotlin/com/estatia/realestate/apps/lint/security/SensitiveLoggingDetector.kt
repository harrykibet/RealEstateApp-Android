package com.estatia.realestate.apps.lint.security

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

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
            
            node.valueArguments.forEach { arg ->
                val argText = arg.asRenderString().lowercase()
                val isSensitiveText = sensitiveKeywords.any { keyword -> argText.contains(keyword) }
                
                var isSensitiveVariable = false
                arg.accept(object : AbstractUastVisitor() {
                    override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression): Boolean {
                        val name = node.identifier.lowercase()
                        if (sensitiveKeywords.any { name.contains(it) }) {
                            isSensitiveVariable = true
                        }
                        return super.visitSimpleNameReferenceExpression(node)
                    }
                })

                if (isSensitiveText || isSensitiveVariable) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Potential exposure of sensitive data in logs. Avoid logging identifiers like 'password', 'token', or 'email'."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "SensitiveLogging",
            description = "Sensitive data found in logs",
            rationale = "Logging sensitive info (passwords, tokens) is a security risk.",
            badExample = "Log.d(\"Auth\", \"Token: \$token\")",
            goodExample = "Log.d(\"Auth\", \"Token received\")",
            category = IssueCategory.SECURITY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.SECURITY,
            architectureLaw = "LAW-010 (Data Privacy)",
            implementation = Implementation(SensitiveLoggingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
