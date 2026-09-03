package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.UVariable

/**
 * Detects usage of "Magic Numbers" in business logic.
 * Mandates extracting literals to named constants.
 */
class MagicNumberDetector : Detector(), SourceCodeScanner {

    private val allowedNumbers = setOf(0, 1, -1, 100, 10, 2, 24, 60, 1000)

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(ULiteralExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitLiteralExpression(node: ULiteralExpression) {
            val value = node.value
            if (value is Number) {
                if (!allowedNumbers.contains(value.toInt())) {
                    val parent = node.uastParent
                    if (parent !is UVariable) { // Allow initialization of constants
                        context.report(
                            ISSUE,
                            node,
                            context.getLocation(node),
                            "Magic number '$value' detected. Extract this to a named constant to provide context."
                        )
                    }
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MagicNumber",
            description = "Magic number detected in logic",
            rationale = "Extract literal numbers to named constants to improve readability.",
            badExample = "if (age > 21) { ... }",
            goodExample = "const val MIN_AGE = 21\nif (age > MIN_AGE) { ... }",
            category = IssueCategory.ARCHITECTURE, // General health
            tier = IssueTier.WARNING,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-001",
            implementation = Implementation(MagicNumberDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
