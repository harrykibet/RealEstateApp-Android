package com.estatia.realestate.apps.lint.security

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * LAW-010: Sensitive Data Protection.
 * Prevents hardcoding of sensitive strings like API keys or secrets.
 * 
 * NOTE: This is a heuristic-based check using keyword matching and string length.
 * It is not a substitute for dedicated secret scanning tools.
 */
class HardcodedSecretsDetector : Detector(), SourceCodeScanner {

    private val secretKeywords = setOf("apikey", "secret", "token", "password", "credential")

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(ULiteralExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitLiteralExpression(node: ULiteralExpression) {
            if (node.isString) {
                val value = node.value as? String ?: ""
                if (value.isNotBlank() && value.length > 5) {
                    val variable = node.getParentOfType<UVariable>(UVariable::class.java, false)
                    if (variable != null) {
                        val name = variable.name?.lowercase() ?: ""
                        if (secretKeywords.any { name.contains(it) }) {
                            context.report(
                                ISSUE,
                                variable as UElement,
                                context.getLocation(variable as UElement),
                                "Potential hardcoded secret detected in variable '${variable.name}'. Move secrets to a secure config or use build variables (LAW-010)."
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "HardcodedSecrets",
            description = "Potential hardcoded secret detected",
            rationale = "Secrets should not be committed to source control. This check is heuristic-based; use dedicated tools like Gitleaks for full coverage.",
            badExample = "val apiKey = \"12345\"",
            goodExample = "val apiKey = BuildConfig.API_KEY",
            category = IssueCategory.SECURITY,
            tier = IssueTier.ERROR,
            owner = RuleOwner.SECURITY,
            architectureLaw = Law.LAW_010,
            implementation = Implementation(HardcodedSecretsDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
