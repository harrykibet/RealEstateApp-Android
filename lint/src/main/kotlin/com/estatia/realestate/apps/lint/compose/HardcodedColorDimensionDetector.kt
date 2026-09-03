package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

/**
 * Prevents hardcoding Color or Dp values. Enforces using the Design System theme.
 */
class HardcodedColorDimensionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("Color", "dp", "sp")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val path = context.file.path.replace("\\", "/")
        if (path.contains("/designsystem/")) return

        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            "Hardcoded color or dimension detected. Use 'EstatiaTheme' values instead to ensure consistency."
        )
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "HardcodedDesignValue",
            description = "Hardcoded Color or Dimension detected",
            rationale = "Hardcoding design values makes the app harder to theme and maintain.",
            badExample = "Modifier.padding(16.dp)",
            goodExample = "Modifier.padding(EstatiaTheme.spacing.medium)",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-022 (Design System)",
            implementation = Implementation(HardcodedColorDimensionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
