package com.estatia.realestate.apps.lint.security

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField
import org.jetbrains.uast.ULiteralExpression

/**
 * Prevents hardcoding of sensitive strings like API keys or secrets.
 */
class HardcodedSecretsDetector : Detector(), SourceCodeScanner {

    private val secretKeywords = setOf("apikey", "secret", "token", "password", "credential")

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val name = node.name.lowercase()
            if (secretKeywords.any { name.contains(it) }) {
                val initializer = node.uastInitializer
                if (initializer is ULiteralExpression && initializer.isString) {
                    val value = initializer.value as? String ?: ""
                    if (value.isNotBlank() && value.length > 5) {
                        context.report(
                            ISSUE,
                            node,
                            context.getLocation(node),
                            "Potential hardcoded secret detected in field '${node.name}'. Move secrets to a secure config or use build variables."
                        )
                    }
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "HardcodedSecret",
            description = "Potential hardcoded secret detected",
            rationale = "Secrets should not be committed to source control.",
            badExample = "val apiKey = \"12345\"",
            goodExample = "val apiKey = BuildConfig.API_KEY",
            category = IssueCategory.SECURITY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.SECURITY,
            architectureLaw = "LAW-010",
            implementation = Implementation(HardcodedSecretsDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
