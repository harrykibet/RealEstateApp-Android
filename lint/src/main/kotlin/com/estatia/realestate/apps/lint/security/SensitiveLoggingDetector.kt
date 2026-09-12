package com.estatia.realestate.apps.lint.security

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.RuleOwner
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
        if (isLoggingLibrary(method)) {
            var reported = false
            node.valueArguments.forEach { arg ->
                if (reported) return@forEach
                
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
                        "Potential exposure of sensitive data in logs. Avoid logging identifiers like 'password', 'token', or 'email' (LAW-010)."
                    )
                    reported = true
                }
            }
        }
    }

    private fun isLoggingLibrary(method: PsiMethod): Boolean {
        val fqn = method.containingClass?.qualifiedName ?: return false
        return fqn == "android.util.Log" || 
               fqn == "timber.log.Timber" ||
               fqn == "timber.log.Timber.Tree"
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "SensitiveLogging",
            description = "Sensitive data found in logs",
            rationale = "Logging sensitive info (passwords, tokens) is a security risk.",
            badExample = "Log.d(\"Auth\", \"Token: \$token\")",
            goodExample = "Log.d(\"Auth\", \"Token received\")",
            category = IssueCategory.SECURITY,
            architectureLaw = Law.LAW_010,
            implementation = Implementation(SensitiveLoggingDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
