package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

/**
 * Detector that prevents logging of potentially sensitive information 
 * (tokens, passwords, secrets, etc.).
 */
class SensitiveLoggingDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("d", "i", "w", "e", "v", "log", "track", "record")

    private val sensitiveKeywords = setOf("token", "password", "secret", "key", "credential", "auth", "email", "phone")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val className = method.containingClass?.qualifiedName
        
        // Target common logging frameworks
        if (className == "android.util.Log" || 
            className?.contains("logger", ignoreCase = true) == true ||
            className?.contains("analytics", ignoreCase = true) == true) {
            
            // Check argument names and literal values
            for (arg in node.valueArguments) {
                val source = arg.sourcePsi?.text?.lowercase() ?: continue
                if (sensitiveKeywords.any { source.contains(it) }) {
                    reportIssue(context, arg, source)
                }
            }
        }
    }

    private fun reportIssue(context: JavaContext, node: UElement, snippet: String) {
        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            "Potential sensitive data leakage in logs: '$snippet'. " +
                    "Never log passwords, tokens, or PII. Use sanitized or hashed identifiers if necessary."
        )
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "SensitiveLogging",
            briefDescription = "Sensitive Data Logging",
            explanation = """
                Logging sensitive data like tokens, passwords, or PII (email, phone) is a security 
                vulnerability. This data can be harvested from device logs or 3rd-party log 
                aggregators. 
                
                Ensure that all logged data is sanitized and contains no private information.
            """.trimIndent(),
            category = Category.SECURITY,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                SensitiveLoggingDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
