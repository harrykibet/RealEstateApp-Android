package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.UElement

/**
 * Ensures that the Estatia Lint JAR is active and running.
 */
class CanaryHeartbeatDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(ULiteralExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitLiteralExpression(node: ULiteralExpression) {
            if (node.value == "DELIBERATE ARCHITECTURAL VIOLATIONS") {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Estatia Lint Canary is Active"
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "LintCanaryActive",
            description = "Estatia Lint is running",
            rationale = "Internal heartbeat to verify custom rules are loaded.",
            badExample = "",
            goodExample = "",
            category = IssueCategory.ARCHITECTURE,
            architectureLaw = Law.LAW_034,
            implementation = Implementation(CanaryHeartbeatDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
