package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Prevents hardcoding strings in Composables. Enforces using string resources for localization.
 */
class HardcodedStringDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(ULiteralExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitLiteralExpression(node: ULiteralExpression) {
            val value = node.value as? String ?: return
            if (value.isBlank()) return

            val containingMethod = node.getParentOfType(UMethod::class.java) ?: return
            val isComposable = context.evaluator.getAnnotations(containingMethod.javaPsi)
                .any { it.qualifiedName?.endsWith("Composable") == true }

            if (isComposable) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Hardcoded string '$value' found in Composable. Move this to strings.xml for localization."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "HardcodedStringInCompose",
            description = "Hardcoded string in Composable",
            rationale = "Hardcoded strings prevent localization and accessibility support.",
            badExample = "Text(\"Hello\")",
            goodExample = "Text(stringResource(R.string.hello))",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-022 (Localization)",
            implementation = Implementation(HardcodedStringDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
